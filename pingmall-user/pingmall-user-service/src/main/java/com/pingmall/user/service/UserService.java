package com.pingmall.user.service;

import com.pingmall.common.utils.NumberUtils;
import com.pingmall.user.mapper.UserMapper;
import com.pingmall.user.pojo.User;
import com.pingmall.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户中心业务层
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;
    //AmqbTemplate用于发送消息到RabbitMQ
    @Autowired
    private AmqpTemplate amqpTemplate;
    //验证码保存在Redis中的Key的前缀
    @Autowired
    private static final String KEY_PREFIX = "user:verify:";
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 用户数据验证
     *
     * @param data
     * @param type
     * @return
     */
    public Boolean checkUser(String data, Integer type) {
        User record = new User();
        switch (type) {
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                return null;
        }
        return userMapper.selectCount(record) == 0;
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    public void sendVerifyCode(String phone) {
        //手机号码验证
        if (StringUtils.isBlank(phone))
            return;
        //生成验证码
        String code = NumberUtils.generateCode(6);
        //发送消息到RabbitMQ（监听器接收消息内容使用Map所以此处发送时也使用Map）
        Map<String, String> msg = new HashMap<>();
        msg.put("phone", phone);
        msg.put("code", code);
        amqpTemplate.convertAndSend("PINGMALL.SMS.EXCHANGE", "verify_code_sms", msg);
        //把验证码保存到Redis
        stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
    }

    /**
     * 注册用户
     *
     * @param user
     * @param code
     * @return
     */
    public void register(User user, String code) {
        //从Redis获取相应的正确验证码
        String rightCode = stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        //校验验证码
        if (!StringUtils.equals(code, rightCode)) {
            return;
        }
        //生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);
        //加盐加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));
        //添加用户数据
        user.setId(null);
        user.setCreated(new Date());
        userMapper.insertSelective(user);
        //注册成功后删除验证码（避免内存浪费）
        stringRedisTemplate.delete(KEY_PREFIX + user.getPhone());
    }

    /**
     * 根据用户名和密码查询用户是否存在
     *
     * @param username
     * @param password
     * @return
     */
    public User findUser(String username, String password) {
        User model = new User();
        model.setUsername(username);
        //先根据用户名查询用户
        User record = userMapper.selectOne(model);
        if (record == null)
            return null;
        //获取盐对用户输入的密码加盐加密
        password = CodecUtils.md5Hex(password, record.getSalt());
        //和数据库中的密码比较
        if (StringUtils.equals(password, record.getPassword()))
            return record;
        return null;
    }
}
