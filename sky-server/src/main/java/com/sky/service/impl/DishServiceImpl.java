package com.sky.service.impl;

import com.sky.anonotation.AutoFill;
import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @AutoFill(value = OperationType.INSERT)
    @Override
    public void save(DishDTO dishDTO) {
        // 创建 Dish 对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 插入数据
        dishMapper.insert(dish);

        // 获取 id
        Long id = dish.getId();

        // 批量插入口味
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if  (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(dishFlavors);
        }

    }
}
