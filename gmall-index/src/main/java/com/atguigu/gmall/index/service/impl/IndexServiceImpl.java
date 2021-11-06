package com.atguigu.gmall.index.service.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.annotation.Cache;
import com.atguigu.gmall.index.feign.GmallPmsClient;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;
import com.google.common.collect.Lists;
import org.apache.commons.lang.StringUtils;
import org.redisson.api.RCountDownLatch;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class IndexServiceImpl implements IndexService {

    private static final String KEY_PREFIX = "index:cates:";

    @Autowired
    private GmallPmsClient pmsClient;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    @Override
    public List<CategoryEntity> queryLevel1Categories() {
        ResponseVo<List<CategoryEntity>> listResponseVo = pmsClient.queryCategoryByParentId(0l);

        return listResponseVo.getData();
    }

    @Override
    @Cache(prefix = KEY_PREFIX,timeout = 10,lock = "lock",random = 1)
    public List<CategoryVo> queryLevel2Categories(Long pid) {

        //如果缓存中没有则查库
        ResponseVo<List<CategoryVo>> listResponseVo = pmsClient.queryCategoryVosByPid(pid);
        return listResponseVo.getData();
    }

    public List<CategoryVo> queryLevel2Categories2(Long pid) {
        //查询缓存 如果缓存中有直接返回
        String json = redisTemplate.opsForValue().get(KEY_PREFIX + pid);
        if (StringUtils.isNotBlank(json)){
            return JSON.parseArray(json, CategoryVo.class);
        }

        RLock lock = redissonClient.getLock("lock" + pid);
        System.out.println("lock.getName() = " + lock.getName());
        lock.lock(1,TimeUnit.MINUTES);

        try {
            TimeUnit.MINUTES.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //如果缓存中没有则查库
        ResponseVo<List<CategoryVo>> listResponseVo = pmsClient.queryCategoryVosByPid(pid);
        //防止不存在的占用太多内存
        if (CollectionUtils.isEmpty(listResponseVo.getData())){
            redisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(listResponseVo.getData()),3, TimeUnit.MINUTES);
        }else {
            //放入缓存中 过期时间长一点
            redisTemplate.opsForValue().set(KEY_PREFIX + pid,JSON.toJSONString(listResponseVo.getData()),43000+new Random().nextInt(3000),TimeUnit.MINUTES);
        }
        return listResponseVo.getData();
    }

    @Override
    public void testLock(){

        RLock lock = redissonClient.getLock("lock");
        lock.lock();

        //业务逻辑
        String numStr = redisTemplate.opsForValue().get("num");
        if (StringUtils.isNotBlank(numStr)){
            int num = Integer.parseInt(numStr);
            redisTemplate.opsForValue().set("num",String.valueOf(++num));
        }

        lock.unlock();
    }

    @Override
    public String testReadLock() {
        RReadWriteLock rlock = redissonClient.getReadWriteLock("rwlock");
        rlock.readLock().lock(10,TimeUnit.SECONDS);
        String msg = redisTemplate.opsForValue().get("msg");
        return msg;
    }

    @Override
    public void testWriteLock() {
        RReadWriteLock wlock = redissonClient.getReadWriteLock("rwlock");
        wlock.writeLock().lock(10,TimeUnit.SECONDS);
        String uuid = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set("msg",uuid);
    }

    @Override
    public String testLatch() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("latch");
        latch.trySetCount(6);
        try {
            latch.await();
            return "班长锁门";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String testCountDown() {
        RCountDownLatch latch = redissonClient.getCountDownLatch("latch");
        latch.countDown();
        return "出来了一位同学";
    }

    public void testLock1(){

        String uuid = UUID.randomUUID().toString();

        //争抢锁 setnx命令
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid,3,TimeUnit.SECONDS);
        //如果拿到锁 执行业务逻辑
        if (lock){
            String numStr = redisTemplate.opsForValue().get("num");
            if (StringUtils.isNotBlank(numStr)){
                int num = Integer.parseInt(numStr);
                redisTemplate.opsForValue().set("num",String.valueOf(++num));
            }
            //LUA脚本
            String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
            redisTemplate.execute(new DefaultRedisScript(script), Arrays.asList("lock"),Arrays.asList(uuid));
            //业务逻辑执行完释放锁
            //redisTemplate.delete("lock");
        }
        else {
            //等待 重试
            try {
                TimeUnit.SECONDS.sleep(3);
                testLock();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
