package com.atguigui.gmall.search;

import com.atguigu.gmall.common.bean.PageParamVo;
import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigu.gmall.pms.entity.SkuEntity;
import com.atguigu.gmall.pms.entity.SpuEntity;
import com.atguigui.gmall.search.bean.Goods;
import com.atguigui.gmall.search.feign.GmallPmsClient;
import com.atguigui.gmall.search.repository.GoodsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.cdi.ElasticsearchRepositoryBean;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GmallSearchApplicationTests {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    @Autowired
    private GmallPmsClient gmallPmsClient;

    @Autowired
    private GoodsRepository goodsRepository;

    @Test
    void contextLoads() {
        System.out.println("contextLoads");
        this.restTemplate.createIndex(Goods.class);
        this.restTemplate.putMapping(Goods.class);
    }

    @Test
    void importData() {
        System.out.println("importData");
        //远程调用pms服务获取spu数据
        Integer pageNum = 1;
        Integer pageSize = 100;

        //分页查询
        do{
            PageParamVo pageParamVo = new PageParamVo();
            pageParamVo.setPageNum(pageNum);
            pageParamVo.setPageSize(pageSize);
            ResponseVo<List<SpuEntity>> spuListResponseVo = gmallPmsClient.querySpuByPage(pageParamVo);
            List<SpuEntity> spuEntities = spuListResponseVo.getData();
            System.out.println(spuEntities);

            List<Goods> goodsList = new ArrayList<>();

            spuEntities.forEach(spuEntity -> {
                ResponseVo<List<SkuEntity>> skuListResponseVo = gmallPmsClient.querySkusBySpuId(spuEntity.getId());
                List<SkuEntity> skuEntities = skuListResponseVo.getData();
                skuEntities.forEach(skuEntity -> {
                    //sku转换成索引实体
                    Goods goods = new Goods();
                    goods.setBrandId(spuEntity.getBrandId());
                    goods.setBrandName(null);
                    goods.setCategoryId(spuEntity.getCatagoryId());
                    goods.setCategoryName(null);
                    goods.setCreateTime(spuEntity.getCreateTime());
                    goodsList.add(goods);
                });
            });

            System.out.println("goodsList = " + goodsList);
            this.goodsRepository.saveAll(goodsList);

            pageNum++;
            pageSize = spuEntities.size();
        }while (pageSize==100);
    }

}
