package com.pingmall.sms.listener;

import com.aliyuncs.exceptions.ClientException;
import com.pingmall.sms.config.SMSProperties;
import com.pingmall.sms.utils.SMSUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component
public class SMSListener {
    //注入smsUtils工具类用于发送短信
    @Autowired
    private SMSUtils smsUtils;
    //注入smsProperties以填充不同的短信内容
    @Autowired
    private SMSProperties smsProperties;

    /**
     * 监听短信消息
     * @param msgMap
     * @throws ClientException
     */
    //声明当前方法是一个消息监听器，绑定内容
    @RabbitListener(bindings =
    @QueueBinding(
            //通过名称绑定一个队列，启用持久化
            value = @Queue(value = "PINGMALL.SMS.QUEUE",durable = "true"),
            //绑定交换机
            exchange = @Exchange(
                    //通过名称绑定
                    value = "PINGMALL.SMS.EXCHANGE",
                    //忽略交换机声明异常
                    ignoreDeclarationExceptions = "true",
                    //把消息队列类型设置为“发布订阅之Topic”
                    type = ExchangeTypes.TOPIC),
            //绑定监听器的RoutingKey
            key = {"verify_code_sms"}
    ))
    public void sendSMS(Map<String, String> msgMap) throws ClientException {
        //非空验证（封装收信人号码和验证码的Map集合）
        if (CollectionUtils.isEmpty(msgMap))
            return;
        String phone = msgMap.get("phone");
        String code = msgMap.get("code");
        //非空验证（收信人号码和验证码）
        if (StringUtils.isBlank(phone) || StringUtils.isBlank(code))
            return;
        //调用工具类方法发送短信
        /*
        * 参数1：收信人号码
        * 参数2：验证码
        * 参数3：短信签名
        * 参数4：短信模板
        * */
        /*smsUtils.sendSms(phone, code, smsProperties.getSignName(),
                smsProperties.getVerifyCodeTemplate());*/
        smsUtils.sendSmsNew(phone,code,smsProperties.getSignName(),smsProperties.getVerifyCodeTemplate());
    }
}
