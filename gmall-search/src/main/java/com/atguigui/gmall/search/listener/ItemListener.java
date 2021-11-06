package com.atguigui.gmall.search.listener;

import com.atguigu.gmall.pms.constant.PmsConstant;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class ItemListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "search-item-queue",durable = "true"),
            exchange = @Exchange(name = PmsConstant.saveSpu_exchange,ignoreDeclarationExceptions = "true",type = "topic"),
            key = {PmsConstant.saveSpu_routeingKey}
    ))
    public void listener(Long spuId, Channel channel, Message message) {
        System.out.println("spuId = " + spuId);
        System.out.println("channel = " + channel);
        System.out.println("message = " + message);
    }
}
