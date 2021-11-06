package com.atguigu.gmall.index.service;

import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.vo.CategoryVo;

import java.util.List;

public interface IndexService {

    List<CategoryEntity> queryLevel1Categories();

    List<CategoryVo> queryLevel2Categories(Long pid);

    void testLock();

    String testReadLock();

    void testWriteLock();

    String testLatch();

    String testCountDown();
}
