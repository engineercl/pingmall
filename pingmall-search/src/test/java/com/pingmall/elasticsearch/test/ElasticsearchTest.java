package com.pingmall.elasticsearch.test;

import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.pojo.bo.SpuBo;
import com.pingmall.repository.GoodsRepository;
import com.pingmall.search.client.GoodsClient;
import com.pingmall.search.pojo.Goods;
import com.pingmall.search.service.SearchService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ElasticsearchTest {
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private SearchService searchService;
    @Autowired
    private GoodsClient goodsClient;

    @Test
    public void test() {
        elasticsearchTemplate.createIndex(Goods.class);
        elasticsearchTemplate.putMapping(Goods.class);
        //添加索引文档数据
        //指定查询页码
        int page = 1;
        //指定每页条数
        int rows = 100;
        do {
            //分页查询Spu
            PageResult<SpuBo> result = goodsClient.getSpuByPage(null, null, page, rows);
            //获取当前页的Spu集合
            List<SpuBo> spuBos = result.getItems();
            /*遍历Spu集合
             *把每个Spu转换成Goods
             *最终获取一个Goods集合
             */
            List<Goods> goodsList = spuBos.stream().map(spuBo -> {
                try {
                    return searchService.buildGoods(spuBo);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }).collect(Collectors.toList());
            //执行添加索引文档数据
            goodsRepository.saveAll(goodsList);
            //继续查询下一页的数据并添加索引文档
            page++;
            //记录每次当前页的总条数
            rows = spuBos.size();
            //当总条数不为100时为最后一页
            //则跳出循环
            //结束查询与添加索引文档的操作
        } while (rows == 100);
    }
}
