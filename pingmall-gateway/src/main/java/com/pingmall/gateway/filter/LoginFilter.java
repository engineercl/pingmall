package com.pingmall.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.pingmall.auth.utils.JwtUtils;
import com.pingmall.common.utils.CookieUtils;
import com.pingmall.gateway.config.FilterProperties;
import com.pingmall.gateway.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 自定义网关过滤器
 * 需要继承netflix的ZuulFilter抽象类
 */
//多个对象必须在同一个容器内才能相互注入
@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {
    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;

    /**
     * 返回值指定过滤器类型
     * pre表示前置
     *
     * @return
     */
    @Override
    public String filterType() {
        return "pre";
    }

    /**
     * 返回值指定过滤器顺序
     * 为了可扩展性设置为10
     * 这样未来在此过滤器之前还能够添加其它过滤器
     *
     * @return
     */
    @Override
    public int filterOrder() {
        return 10;
    }

    /**
     * 返回值指定是否执行过滤
     *
     * @return
     */
    @Override
    public boolean shouldFilter() {
        String requestURL = RequestContext.getCurrentContext().getRequest().getRequestURL().toString();
        List<String> allowPaths = filterProperties.getAllowPaths();
        for (String allowPath : allowPaths) {
            if (StringUtils.contains(requestURL, allowPath))
                return false;
        }
        return true;
    }

    /**
     * 过滤规则（业务逻辑）
     *
     * @return
     * @throws ZuulException
     */
    @Override
    public Object run() throws ZuulException {
        //初始化Zuul应用上下文对象
        RequestContext context = RequestContext.getCurrentContext();
        //通过Zuul应用上下文对象获取Request对象
        HttpServletRequest request = context.getRequest();
        //获取Cookie中的JwtToken
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            //解析JwtToken
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            //通过Zuul应用上下文对象设置不把请求转发给网关
            context.setSendZuulResponse(false);
            //设置响应状态码（未授权）
            context.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
            e.printStackTrace();
        }
        //正常解析JwtToken（直接放行）
        return null;
    }
}
