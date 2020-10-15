package com.pingmall.goods.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

@Service
public class GoodsHtmlService {
    //Spring已经帮我们创建了一个模板引擎对象到容器中
    @Autowired
    private TemplateEngine templateEngine;
    @Autowired
    private GoodsService goodsService;

    /**
     * 创建商品详情页静态资源
     * 需要通过spuId确定一个商品
     *
     * @param spuId
     */
    public void creteHtml(Long spuId) {
        //初始化运行上下文对象（数据模型需要设置到此对象中）
        Context context = new Context();
        //数据模型可以是一个Map集合
        //使用GoodsService获取数据模型
        context.setVariables(goodsService.loadData(spuId));
        //需要使用流对象在nginx服务器中写入静态资源文件
        PrintWriter printWriter = null;
        try {
            File file = new File
                    //静态资源一般存放在nginx的html目录下
                    //静态资源文件名使用spuId（一个Spu对应一个静态资源）
                    ("D:\\nginx-1.14.0\\html\\item\\" + spuId + ".html");
            printWriter = new PrintWriter(file);
            //生成静态资源（参数分别是：“模板名称”，“添加了数据模型的上下文对象”，“指定了文件对象的流对象”）
            templateEngine.process("item", context, printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            //不为空表示流对象成功创建
            if (printWriter != null)
                //关闭流对象（释放资源）
                printWriter.close();
        }
    }

    /**
     * 删除商品详情页静态资源
     * 需要通过spuId确定一个商品
     *
     * @param id
     */
    public void deleteHtml(Long id) {
        File file = new File("D:\\nginx-1.14.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
