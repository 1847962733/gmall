package com.atguigu.gmall.pms.vo;

import com.atguigu.gmall.pms.entity.SpuAttrValueEntity;
import lombok.Data;
import java.util.List;

@Data
public class BaseAttrsVo  extends SpuAttrValueEntity {

    private List<String> valueSelected;

    public void setValueSelected(List<String> valueSelected) {

        this.setAttrValue(String.join(",",valueSelected));
    }
}
