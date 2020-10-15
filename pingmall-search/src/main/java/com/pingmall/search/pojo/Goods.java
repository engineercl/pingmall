package com.pingmall.search.pojo;

import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Document(indexName = "goods", type = "docs", shards = 1, replicas = 0)
public class Goods {
    // spuId
    @Id
    private Long id;

    // 所有需要被搜索的信息，包含标题，分类，甚至品牌
    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all;

    // 卖点
    @Field(type = FieldType.Keyword, index = false)
    private String subTitle;

    // 品牌id
    private Long brandId;

    // 1级分类id
    private Long cid1;

    // 2级分类id
    private Long cid2;

    // 3级分类id
    private Long cid3;

    // 创建时间
    private Date createTime;

    // 价格
    private List<Long> price;

    // List<sku>信息的json结构
    @Field(type = FieldType.Keyword, index = false)
    private String skus;

    // 可搜索的规格参数，key是参数名，值是参数值
    private Map<String, Object> specs;
}