package com.atguigu.gmall.index.controller;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.index.service.IndexService;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("index")
public class IndexController {

    @Autowired
    IndexService indexService;

    @GetMapping("/test/lock")
    public void testLock(){
        indexService.testLock();
    }

    @GetMapping("/test/read")
    public ResponseVo<String> testReadLock(){
        String msg = indexService.testReadLock();
        return ResponseVo.ok(msg);
    }

    @GetMapping("/test/write")
    public void testWriteLock(){
        indexService.testWriteLock();
    }

    @GetMapping("/test/latch")
    public ResponseVo<String> testLatch(){
        String msg = indexService.testLatch();
        return ResponseVo.ok(msg);
    }

    @GetMapping("/test/countDown")
    public ResponseVo<String> testCountDown(){
        String msg = indexService.testCountDown();
        return ResponseVo.ok(msg);
    }

    //获取首页侧边一级菜单
    @GetMapping("/cates")
    public ResponseVo<List<CategoryEntity>> cates(){
        List<CategoryEntity> categoryEntities = indexService.queryLevel1Categories();
        return ResponseVo.ok(categoryEntities);
    }

    //获取某个菜单的子菜单
    @GetMapping("/cates/{pid}")
    public ResponseVo<List<CategoryVo>> cates(@PathVariable(value = "pid") Long pid){
        List<CategoryVo> categoryEntities = indexService.queryLevel2Categories(pid);
        return ResponseVo.ok(categoryEntities);
    }
}
