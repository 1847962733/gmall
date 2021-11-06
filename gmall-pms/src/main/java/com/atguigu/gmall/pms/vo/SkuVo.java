package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SkuEntity;
import lombok.Data;

import java.util.List;

@Data
public class SkuVo extends SkuEntity {

    private int stock;
    /**
     * sku积分相关信息
     */
    private String growBounds;
    private String buyBounds;
    private List<Integer> work;

    /**
     * sku满减相关信息
     */
    private String fullPrice;
    private String reducePrice;
    private int fullAddOther;

    /**
     * sku打折信息
     */
    private String fullCount;
    private String discount;
    private int ladderAddOther;

    private List<String> images;
    private List<SaleAttrsVo> saleAttrs;

}
