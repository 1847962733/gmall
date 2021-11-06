package com.atguigu.gmall.pms.feign;

import com.atguigui.gmall.sms.api.GmallSmsApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("sms-service")
public interface SmsFeignService extends GmallSmsApi {
}
