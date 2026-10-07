package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查询套餐数量
     */
    @Select("select count(*) from setmeal_dish where dish_id = #{id}")
    Long countByDishId(Long id);

    /**
     * 批量插入套餐菜品关联数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);
}
