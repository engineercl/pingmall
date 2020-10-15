package com.pingmall.search.listener;

import com.pingmall.search.service.SearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 商品消息监听器
 */
@Component
public class GoodsListener {
    @Autowired
    private SearchService searchService;

    /**
     * 监听新增、修改消息
     * 做同步处理
     * @param id
     */
    //声明此方法是一个RabbitMQ的消息消费方（监听方）
    //并且绑定队列
    @RabbitListener(bindings = @QueueBinding(
            //通过队列名称绑定，开启持久化
            value = @Queue(value = "PINGMALL.ITEM.SAVE.QUEUE",durable = "true"),
            //通过名称绑定交换机，忽略声明交换机异常，消息队列类型使用“发布订阅.Topic”
            exchange = @Exchange(value = "PINGMALL.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",type = ExchangeTypes.TOPIC),
            //绑定routingKey
            key = {"item.insert","item.update"}
    ))
    public void save(Long id) throws IOException {
        //非空验证
        if (id == null)
            return;

        //保存索引数据
        searchService.save(id);
    }

    /**
     * 监听删除消息
     * 做同步处理
     * @param id
     */
    //声明此方法是一个RabbitMQ的消息消费方（监听方）
    //并且绑定队列
    @RabbitListener(bindings =
    @QueueBinding(
            //通过队列名称绑定，开启持久化
            value = @Queue(value = "PINGMALL.ITEM.DELETE.QUEUE",durable = "true"),
            //通过名称绑定交换机，忽略声明交换机异常，消息队列类型使用“发布订阅.Topic”
            exchange = @Exchange(
                    value = "PINGMALL.ITEM.EXCHANGE",
                    ignoreDeclarationExceptions = "true",
                    type = ExchangeTypes.TOPIC),
            //绑定routingKey
            key = {"item.delete"}
    ))
    public void delete(Long id) throws IOException {
        //非空验证
        if (id == null)
            return;

        //删除索引数据
        searchService.delete(id);
    }
}
