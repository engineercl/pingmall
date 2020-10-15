package com.pingmall.item.mapper;

import com.pingmall.item.pojo.Brand;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * 持久层
 */
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 添加数据到类目品牌中间表
     *
     * @param cid
     */
    @Insert("INSERT INTO tb_category_brand VALUES (#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 通过中间表查询指定类目下的所有品牌
     *
     * @param cid
     * @return
     */
    @Select("SELECT\n" +
            "\ttb_brand.id,\n" +
            "\ttb_brand.`name`,\n" +
            "\ttb_brand.image,\n" +
            "\ttb_brand.letter \n" +
            "FROM\n" +
            "\ttb_brand \n" +
            "WHERE\n" +
            "\ttb_brand.id IN ((\n" +
            "\t\tSELECT\n" +
            "\t\t\ttb_category_brand.brand_id \n" +
            "\t\tFROM\n" +
            "\t\t\ttb_category_brand \n" +
            "\t\tWHERE\n" +
            "\t\ttb_category_brand.category_id = #{cid} \n" +
            "\t))")
    List<Brand> selectBrandsByCId(Long cid);
}
