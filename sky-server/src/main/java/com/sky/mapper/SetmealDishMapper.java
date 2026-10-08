package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id计算关联的套餐数
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Long countByDishId(Long id);

    /**
     * 批量插入套餐菜品关联数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询菜品列表
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);

    /**
     * 根据套餐id删除套餐和菜品的关联数据
     */
    void deleteBySetmealIds(List<Long> ids);
}
