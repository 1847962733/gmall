package com.atguigui.gmall.sms.api;

import com.atguigu.gmall.common.bean.ResponseVo;
import com.atguigui.gmall.sms.to.SkuBoundsTo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public interface GmallSmsApi {

    @PostMapping("sms/skubounds")
    ResponseVo<Object> save(@RequestBody SkuBoundsTo spuBounds);
}
