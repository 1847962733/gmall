package com.atguigu.gmall.sms.mapper;

import com.atguigu.gmall.sms.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author yaolong
 * @email yaolong@atguigu.com
 * @date 2021-10-17 15:53:30
 */
@Mapper
public interface CouponMapper extends BaseMapper<CouponEntity> {
	
}
