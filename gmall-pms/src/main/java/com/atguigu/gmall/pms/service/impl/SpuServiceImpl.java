package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.constant.PmsConstant;
import com.atguigu.gmall.pms.entity.*;
import com.atguigu.gmall.pms.feign.SmsFeignService;
import com.atguigu.gmall.pms.service.*;
import com.atguigu.gmall.pms.vo.BaseAttrsVo;
import com.atguigu.gmall.pms.vo.SaleAttrsVo;
import com.atguigu.gmall.pms.vo.SkuVo;
import com.atguigu.gmall.pms.vo.SpuVo;
import com.atguigui.gmall.sms.to.SkuBoundsTo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.SpuMapper;
import org.springframework.util.CollectionUtils;


@Service("spuService")
public class SpuServiceImpl extends ServiceImpl<SpuMapper, SpuEntity> implements SpuService {

    @Autowired
    private SpuDescService spuDescService;

    @Autowired
    private SpuAttrValueService spuAttrValueService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private SkuAttrValueService skuAttrValueService;

    @Autowired
    private SmsFeignService smsFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<SpuEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<SpuEntity>()
        );

        return new PageResultVo(page);
    }

    @Override
    public PageResultVo querySpusByCidPage(PageParamVo paramVo, Long categoryId) {

        QueryWrapper<SpuEntity> wrapper = new QueryWrapper<>();

        if (categoryId != null && categoryId != 0) {
            wrapper.eq("catagory_id", categoryId);
        }

        String key = paramVo.getKey();
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(t -> {
                t.eq("id", key).or().like("name", key);
            });
        }

        IPage<SpuEntity> page = this.page(paramVo.getPage(), wrapper);

        return new PageResultVo(page);
    }

    @Override
    public void bigSave(SpuVo spu) {
        //1.保存spu的相关信息

        //1.1. 保存spu的信息 pms_spu
        SpuEntity spuEntity = new SpuEntity();
        BeanUtils.copyProperties(spu, spuEntity);
        spuEntity.setCreateTime(new Date());
        spuEntity.setUpdateTime(new Date());
        this.save(spuEntity);

        //1.2. 保存spu的描述信息 pms_spu_desc
        SpuDescEntity spuDescEntity = new SpuDescEntity();
        spuDescEntity.setSpuId(spuEntity.getId());
        //if (!CollectionUtils.isEmpty(spu.getSpuImages()))
        spuDescEntity.setDecript(StringUtils.join(spu.getSpuImages(), ","));
        spuDescService.save(spuDescEntity);


        //1.3. 保存spu的基本属性 pms_spu_attr_value
        List<BaseAttrsVo> baseAttrs = spu.getBaseAttrs();
        if (!CollectionUtils.isEmpty(baseAttrs)) {
            List<SpuAttrValueEntity> collect = baseAttrs.stream().map(attr -> {
                SpuAttrValueEntity attrValueEntity = new SpuAttrValueEntity();
                BeanUtils.copyProperties(attr, attrValueEntity);
                attrValueEntity.setSpuId(spuEntity.getId());
                return attrValueEntity;
            }).collect(Collectors.toList());
            spuAttrValueService.saveBatch(collect);
        }

        //2.保存sku的相关信息
        List<SkuVo> skus = spu.getSkus();
        //2.1. 保存sku的信息 pms_sku
        if (!CollectionUtils.isEmpty(skus)) {

            skus.forEach(skuVo -> {
                skuVo.setCatagoryId(spuEntity.getCatagoryId());
                skuVo.setBrandId(spuEntity.getBrandId());
                skuVo.setSpuId(spuEntity.getId());
                skuService.save(skuVo);
                //2.2. 保存sku的图片信息 pms_sku_images
                //2.3. 保存sku的销售属性 pms_sku_attr_value
                List<SaleAttrsVo> saleAttrs = skuVo.getSaleAttrs();
                if (!CollectionUtils.isEmpty(saleAttrs)) {
                    List<SkuAttrValueEntity> valueEntities = saleAttrs.stream().map(attr -> {
                        SkuAttrValueEntity attrValueEntity = new SkuAttrValueEntity();
                        BeanUtils.copyProperties(attr, attrValueEntity);
                        attrValueEntity.setSkuId(skuVo.getId());
                        return attrValueEntity;
                    }).collect(Collectors.toList());
                    skuAttrValueService.saveBatch(valueEntities);
                }

                //3.保存sku的营销信息
                SkuBoundsTo skuBoundsTo = new SkuBoundsTo();
                skuBoundsTo.setSkuId(skuVo.getId());
                skuBoundsTo.setBuyBounds(new BigDecimal(skuVo.getBuyBounds()));
                skuBoundsTo.setGrowBounds(new BigDecimal(skuVo.getGrowBounds()));
                try{
                    smsFeignService.save(skuBoundsTo);
                }catch (Exception e){
                    log.error("smsFeignService出错",e);
                }
            });

        }

        //生产者发送消息到队列
        rabbitTemplate.convertAndSend(PmsConstant.saveSpu_exchange,PmsConstant.saveSpu_routeingKey,spuEntity.getId());
    }

}