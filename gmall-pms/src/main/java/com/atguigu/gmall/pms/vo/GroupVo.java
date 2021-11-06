package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.AttrEntity;
import com.atguigu.gmall.pms.entity.AttrGroupEntity;

import java.util.List;

public class GroupVo extends AttrGroupEntity {

    private List<AttrEntity> attrEntities;

    public List<AttrEntity> getAttrEntities() {
        return attrEntities;
    }

    public void setAttrEntities(List<AttrEntity> attrEntities) {
        this.attrEntities = attrEntities;
    }

}
