package com.pingmall.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pingmall.item.pojo.*;
import com.pingmall.repository.GoodsRepository;
import com.pingmall.search.client.BrandClient;
import com.pingmall.search.client.CategoryClient;
import com.pingmall.search.client.GoodsClient;
import com.pingmall.search.client.SpecificationClient;
import com.pingmall.search.pojo.Goods;
import com.pingmall.search.pojo.SearchRequest;
import com.pingmall.search.pojo.SearchResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 搜索业务层
 */
@Service
public class SearchService {
    @Autowired
    private CategoryClient categoryClient;
    @Autowired
    private BrandClient brandClient;
    @Autowired
    private GoodsClient goodsClient;
    @Autowired
    private SpecificationClient specificationClient;

    @Autowired
    private GoodsRepository goodsRepository;

    //JSON工具类
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 从Spu构建Goods
     *
     * @param spu
     * @return
     */
    public Goods buildGoods(Spu spu) throws IOException {
        //根据类目ID查询所有类目名称
        List<String> categoryNames = categoryClient.getNamesByIds(
                Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));

        //根据品牌ID查询品牌对象
        Brand brand = brandClient.getByBId(spu.getBrandId());

        //根据Spu查询所有Sku
        List<Sku> skus = goodsClient.getSkusBySpuId(spu.getId());

        //用于JSON序列化的Sku集合
        List<Map<String, Object>> skuList = new ArrayList<>();

        //用于保存所有Sku价格的集合
        List<Long> prices = new ArrayList<>();

        //遍历所有查询到的Sku
        skus.forEach(sku -> {
            //获取价格添加到prices集合
            prices.add(sku.getPrice());
            //每个Sku需要一个对应的Map
            Map<String, Object> skuMap = new HashMap<>();
            //添加Sku必要字段（title，id，images，price）
            skuMap.put("title", sku.getTitle());
            skuMap.put("id", sku.getId());
            /* 首先判断当前Sku图片路径是否为空
             * 如果为空image字段保存一个空字符串
             * 如果非空切割并获取第一个图片路径
             * */
            skuMap.put("image", StringUtils.isBlank(sku.getImages()) ? "" :
                    StringUtils.split(sku.getImages(), ",")[0]);
            skuMap.put("price", sku.getPrice());
            //添加当前Sku对应的Map到skuList
            skuList.add(skuMap);
        });

        //根据三级类目查询所有用于搜索的规格参数
        //获取规格参数名称
        List<SpecParam> specParams = specificationClient.getSpecParams
                (null, spu.getCid3(), null, true);

        //获取规格参数值
        //规格参数值存储在SpuDetail中
        //所以先查询SpuDetail
        SpuDetail spuDetail = goodsClient.getSpuDetail(spu.getId());

        /*使用ObjectMapper的readValue反序列化
         * 第一个参数是要反序列化的JSON数据
         * 第二个参数是指定反序列化的类型
         * 使用TypeReference的匿名实现类来指定泛型
         * 需要抛出一个异常
         * 这里泛型使用Map<String,Object>类型
         * */
        //genericParamsMap是通用规格参数的键值集合（主要用于获取参数值）
        Map<String, Object> genericParamsMap =
                MAPPER.readValue(spuDetail.getGenericSpec(),
                        new TypeReference<Map<String, Object>>() {
                        });
        //specialSpecsMap是特殊规格参数的键值集合（主要用于获取参数值）
        Map<String, List<Object>> specialSpecsMap =
                MAPPER.readValue(spuDetail.getSpecialSpec(),
                        new TypeReference<Map<String, List<Object>>>() {
                        });

        //把两种规格参数添加到新的Map中
        Map<String, Object> specs = new HashMap<>();
        specParams.forEach(specParam -> {
            //判断是否是通用类型
            if (specParam.getGeneric()) {
                //获取通用参数值
                String value = genericParamsMap.get(specParam.getId().toString()).toString();
                //判断是否是数值类型
                if (specParam.getNumeric()) {
                    //获取对应的范围
                    value = chooseSegment(value, specParam);
                }
                //添加到specs集合
                specs.put(specParam.getName(), value);
                //是特殊类型
            } else {
                //获取特殊参数值（特殊参数值是购买时的可选项）
                //可选项存在多个所以使用集合接收
                List<Object> value = specialSpecsMap.get(specParam.getId().toString());
                //添加到specs集合
                specs.put(specParam.getName(), value);
            }
        });

        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //all字段就是搜索使用的内容（需要标题、所有类目名称、品牌名称）
        //注意不同字段要用空格分隔
        //避免拼接出错误的关键字
        goods.setAll(spu.getTitle() + " " +
                StringUtils.join(categoryNames, " ") + " " + brand.getName());
        //设置所有Sku价格
        goods.setPrice(prices);
        //设置所有Sku（先转换为JSON格式）
        try {
            goods.setSkus(MAPPER.writeValueAsString(skuList));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //设置所有用于聚合查询的规格参数（先转换为Map集合）
        //genericParamsMap是通用规格参数的键值（键是参数ID）
        //specialSpecsMap是特殊规格参数的键值（键是参数ID）
        goods.setSpecs(specs);
        return goods;
    }

    /**
     * 根据具体的数值规格参数获取所在范围
     *
     * @param value
     * @param p
     * @return
     */
    private String chooseSegment(String value, SpecParam p) {
        //把具体的数值规格参数转换为double类型（因为此处使用double类型就不会损失精度）
        double val = NumberUtils.toDouble(value);
        //所在范围默认设为“其它”
        String result = "其它";
        //遍历所有范围（使用“,”分割）
        for (String segment : p.getSegments().split(",")) {
            //获取当前范围（使用“-”分割为两个数值字符串）
            String[] segs = segment.split("-");
            //分别获取当前范围两个数值（最小值和最大值）
            double begin = NumberUtils.toDouble(segs[0]);
            //如果获取的是最后一个范围就没有最大值（所以先设置为double类型最大值防止数组越界）
            double end = Double.MAX_VALUE;
            //范围有两个数值
            if (segs.length == 2) {
                //范围最大值重新赋值为第二个数值
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if (val >= begin && val < end) {
                //判断数值是否只有一个
                if (segs.length == 1) {
                    //只有一个时就是一个范围内的最大值（所以拼接单位和“以上”）
                    result = segs[0] + p.getUnit() + "以上";
                    //判断最小值是否为0
                } else if (begin == 0) {
                    //最小值为0就处在第一个范围（所以拼接第二个数值和单位和“以下”）
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    //正常情况（直接拼接所在范围和单位）
                    result = segment + p.getUnit();
                }
                //获取范围结束（跳出循环）
                break;
            }
        }
        //返回所在范围
        return result;
    }

    /**
     * 根据搜索内容分页查询商品信息
     *
     * @param request
     * @return
     */
    public SearchResult search(SearchRequest request) {
        String key = request.getKey();
        if (StringUtils.isBlank(key))
            return null;
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件（把用户输入的关键字和索引库商品文档被搜索字段进行匹配）
        //使用AND拼接查询条件（求交集）
        //basicQuery是基本查询条件
        /*QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key)
                .operator(Operator.AND);*/
        //构建布尔查询构建器
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(request);
        queryBuilder.withQuery(basicQuery);
        //添加分页（页码从0开始）
        queryBuilder.withPageable(PageRequest.of
                (request.getPage() - 1, request.getSize()));
        //结果集过滤（筛选需要字段）
        //通过分析页面得出需要的字段有三个（id，skus，subTitle）
        queryBuilder.withSourceFilter(new FetchSourceFilter
                (new String[]{"id", "skus", "subTitle"}, null));
        //三级类目聚合名称
        String categoryAggName = "categories";
        //品牌聚合名称
        String brandAggName = "brands";
        //添加三级类目聚合查询
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        //添加品牌聚合查询
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));
        //执行查询（把结果集强转为AggregatedPage以便接收结果集中的聚合部分）
        AggregatedPage<Goods> page = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        //解析类目聚合结果集
        List<Map<String, Object>> categoryAgg = getCategoryAgg(page.getAggregation(categoryAggName));
        //解析品牌聚合结果集
        List<Brand> brandAgg = getBrandAgg(page.getAggregation(brandAggName));
        /*
         * 添加规格参数聚合查询
         * 1.规格参数内容过多但和类目关联
         * 2.所以只存在一个类目时再做规格参数聚合
         * 3.也就是判断类目聚合长度是否为1
         * */
        List<Map<String, Object>> specAgg = new ArrayList<>();
        if (!CollectionUtils.isEmpty(categoryAgg) && categoryAgg.size() == 1) {
            /*
             * 获取规格参数聚合结果集
             * 需要参数
             * 1.当前类目ID
             * 2.基本查询条件
             * */
            specAgg = getSpecAgg((Long) categoryAgg.get(0).get("id"), basicQuery);
        }
        //封装为自定义的分页对象返回
        /*第一个参数：总条数
         *第二个参数：总页数
         *第三个参数：结果集
         */
        return new SearchResult(page.getTotalElements(),
                page.getTotalPages(),
                page.getContent(), categoryAgg, brandAgg, specAgg);
    }

    /**
     * 构建布尔查询构建器
     *
     * @param request
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest request) {
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //添加搜索查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", request.getKey())
                .operator(Operator.AND));
        //添加过滤查询条件
        //获取用户选择的过滤条件
        Map<String, Object> filter = request.getFilter();
        //遍历过滤条件
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            String key = entry.getKey();
            if (!StringUtils.equals(key, "brandId") && !StringUtils.equals(key, "cid3"))
                key = "specs." + key + ".keyword";
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, entry.getValue()));
        }
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件和当前类目获取规格参数聚合结果集
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getSpecAgg(Long cid, QueryBuilder basicQuery) {
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);
        //查询要聚合的规格参数
        List<SpecParam> specParams = specificationClient.getSpecParams(null, cid, null, true);
        //添加规格参数聚合查询
        specParams.forEach(specParam -> {
            queryBuilder.addAggregation(AggregationBuilders.terms(specParam.getName())
                    .field("specs." + specParam.getName() + ".keyword"));
        });
        //过滤结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));
        //执行查询（把返回结果强转为聚合类型）
        AggregatedPage<Goods> specAgg = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());
        //解析结果集（最终转换成页面需要的数据格式List<Map<String, Object>>）
        List<Map<String, Object>> specs = new ArrayList<>();
        //获取结果集中所有聚合（内部数据格式是（key：聚合名称/规格参数名称，value：聚合对象））
        Map<String, Aggregation> aggregationMap = specAgg.getAggregations().asMap();
        for (Map.Entry<String, Aggregation> entry : aggregationMap.entrySet()) {
            //每个聚合以一个Map存储
            Map<String, Object> map = new HashMap<>();
            map.put("k", entry.getKey());
            //map的值对应的是此规格参数名称下的所有可选项（也就是桶数组的所有key）
            /*
             * 1.先获取聚合对象（转换为聚合对应的数据类型）
             * 2.通过聚合对象获取所有桶
             * 3.遍历桶把每个key添加到一个List集合
             * 4.最后把List添加到map
             * */
            StringTerms aggregation = (StringTerms) entry.getValue();
            List<String> options = new ArrayList<>();
            aggregation.getBuckets().forEach(bucket -> {
                String option_str = bucket.getKeyAsString();
                if (StringUtils.isNotBlank(option_str))
                    options.add(option_str);
            });
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }

    /**
     * 解析品牌聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAgg(Aggregation aggregation) {
        //把相应的聚合对象强转为聚合数据类型（此处聚合类型就是Long类型词条）
        LongTerms longTerms = (LongTerms) aggregation;
        /*
         * 1.获取桶
         * 2.遍历桶
         * 3.获取每个品牌ID
         * 4.根据品牌ID查询品牌对象
         * 5.添加到集合返回
         * */
        return longTerms.getBuckets().stream().map(bucket -> {
            return brandClient.getByBId(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析类目聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAgg(Aggregation aggregation) {
        //把相应的聚合对象强转为聚合数据类型（此处聚合类型就是Long类型词条）
        LongTerms longTerms = (LongTerms) aggregation;
        /*
         * 1.获取桶
         * 2.遍历桶
         * 3.使用每个Key（categoryId）去查询对应的Category
         * 4.把每个Category的id和name添加到Map
         * 5.把每个Map添加到List
         * 6.返回List
         * */
        return longTerms.getBuckets().stream().map(bucket -> {
            Map<String, Object> map = new HashMap<>();
            Long categoryId = bucket.getKeyAsNumber().longValue();
            List<String> categoryNames = categoryClient.getNamesByIds(Arrays.asList(categoryId));
            map.put("id", categoryId);
            map.put("name", categoryNames.get(0));
            return map;
        }).collect(Collectors.toList());
    }

    /**
     * 保存索引数据
     *
     * @param id
     */
    public void save(Long id) throws IOException {
        //构建索引数据需要一个Goods对象
        Goods goods = null;
        //根据spuId查询到Spu对象，再通过Spu对象构建一个Goods对象
        goods = buildGoods(goodsClient.getSpuById(id));
        //保存索引数据
        goodsRepository.save(goods);
    }

    /**
     * 删除索引数据
     * @param id
     */
    public void delete(Long id) {
        goodsRepository.deleteById(id);
    }
}
