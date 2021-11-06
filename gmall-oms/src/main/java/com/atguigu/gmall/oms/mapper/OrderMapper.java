package com.atguigu.gmall.oms.mapper;

import com.atguigu.gmall.oms.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author yaolong
 * @email yaolong@atguigu.com
 * @date 2021-10-17 15:49:54
 */
@Mapper
public interface OrderMapper extends BaseMapper<OrderEntity> {
	
}
