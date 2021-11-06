package com.atguigu.gmall.pms.service.impl;

import com.atguigu.gmall.pms.vo.CategoryVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.common.bean.PageResultVo;
import com.atguigu.gmall.common.bean.PageParamVo;

import com.atguigu.gmall.pms.mapper.CategoryMapper;
import com.atguigu.gmall.pms.entity.CategoryEntity;
import com.atguigu.gmall.pms.service.CategoryService;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, CategoryEntity> implements CategoryService {

    @Override
    public PageResultVo queryPage(PageParamVo paramVo) {
        IPage<CategoryEntity> page = this.page(
                paramVo.getPage(),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageResultVo(page);
    }

    /**
     * 通过分类父节点id查询子分类
     *
     * @param parentId 父节点id -1：查询所有
     * @return
     */
    @Override
    public List<CategoryEntity> queryCategoryByParentId(Long parentId) {
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper<>();
        if (parentId != null && parentId != -1) {
            queryWrapper.eq("parent_id", parentId);
        }
        return this.list(queryWrapper);
    }

    @Override
    public List<CategoryVo> queryCategoryVosByPid(Long pid) {

        return this.baseMapper.queryCategoryVosByPid(pid);
    }

}