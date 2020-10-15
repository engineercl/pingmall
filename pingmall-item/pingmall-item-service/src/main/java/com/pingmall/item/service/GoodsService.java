package com.pingmall.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.pingmall.common.pojo.PageResult;
import com.pingmall.item.mapper.*;
import com.pingmall.item.pojo.Sku;
import com.pingmall.item.pojo.Spu;
import com.pingmall.item.pojo.SpuDetail;
import com.pingmall.item.pojo.Stock;
import com.pingmall.item.pojo.bo.SpuBo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品业务层
 */
@Service
public class GoodsService {
    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private BrandMapper brandMapper;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private SkuMapper skuMapper;
    @Autowired
    private StockMapper stockMapper;
    //注入AMQP模板对象
    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 分页查询商品信息
     *
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> findSpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        //创建查询模板对象
        Example example = new Example(Spu.class);
        //创建查询条件对象
        Example.Criteria criteria = example.createCriteria();
        //添加查询条件
        if (StringUtils.isNotBlank(key))
            criteria.andLike("title", "%" + key + "%");
        //添加上下架的过滤条件
        if (saleable != null)
            criteria.andEqualTo("saleable", saleable);
        //添加分页条件
        PageHelper.startPage(page, rows);
        //执行查询
        List<Spu> spus = spuMapper.selectByExample(example);
        //创建pageInfo对象
        PageInfo<Spu> pageInfo = new PageInfo<>(spus);
        //Spu集合转换成Spu集合
        //遍历spus同时执行函数
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            //新建一个spuBo对象
            SpuBo spuBo = new SpuBo();
            //把当前spu所有属性值复制到spuBo
            BeanUtils.copyProperties(spu, spuBo);
            //查询品牌
            spuBo.setBname(brandMapper.selectByPrimaryKey(spu.getBrandId()).getName());
            //查询类目
            List<String> categoryNames = categoryService.findByCategoryIds(Arrays.asList(spuBo.getCid1(), spuBo.getCid2(), spuBo.getCid3()));
            spuBo.setCname(StringUtils.join(categoryNames, "-"));
            return spuBo;
        }).collect(Collectors.toList());
        //返回分页对象
        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 保存商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void add(SpuBo spuBo) {
        //需要按照顺序在4张表里添加数据
        //防止恶意注入
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        //spu表
        spuMapper.insertSelective(spuBo);
        //spu_detail表
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insert(spuDetail);
        //sku表和stock表
        addSkusAndStocks(spuBo);
        //发送消息
        sendMessage("insert", spuBo.getId());
    }

    /**
     * 发送消息到RabbitMQ
     *
     * @param type
     * @param id
     */
    private void sendMessage(String type, Long id) {
        try {
            //发送消息（参数分别是routingKey和消息内容）
            amqpTemplate.convertAndSend("item." + type, id);
        } catch (AmqpException e) {
            e.printStackTrace();
        }
    }

    /**
     * 遍历一个spuBo，把它包含的sku和stock添加到数据库
     *
     * @param spuBo
     */
    private void addSkusAndStocks(SpuBo spuBo) {
        //循环插入sku表和stock表的数据
        spuBo.getSkus().forEach(sku -> {
            //sku表
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);
            //stock表
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail
     *
     * @param spuId
     * @return
     */
    public SpuDetail findSpuDetail(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询所有sku
     *
     * @param spuId
     * @return
     */
    public List<Sku> findSkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        //查询所有sku的库存信息
        skus.forEach(sku1 -> {
            Stock stock = stockMapper.selectByPrimaryKey(sku1.getId());
            sku1.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 更新商品信息
     *
     * @param spuBo
     * @return
     */
    @Transactional
    public void edit(SpuBo spuBo) {
        //1.先查询到spu对应的sku数据
        List<Sku> skus = findSkusBySpuId(spuBo.getId());

        //2.遍历对应的原skus
        skus.forEach(sku -> {
            //2.1删除stock表里对应数据
            stockMapper.deleteByPrimaryKey(sku.getId());
            //2.2删除sku表里对应数据
            skuMapper.delete(sku);
        });

        //3.添加新数据到sku表和stock表
        addSkusAndStocks(spuBo);

        //4.更新spu_detail表数据
        spuDetailMapper.updateByPrimaryKey(spuBo.getSpuDetail());

        //5.更新spu表数据
        spuBo.setCreateTime(null);
        spuBo.setLastUpdateTime(new Date());
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //发送消息
        sendMessage("update", spuBo.getId());
    }

    /**
     * 根据ID查询Spu
     *
     * @param spuId
     * @return
     */
    public Spu findSpuById(Long spuId) {
        return spuMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据skuId查询Sku
     */
    public Sku findSkuById(Long skuId) {
        return skuMapper.selectByPrimaryKey(skuId);
    }
}
