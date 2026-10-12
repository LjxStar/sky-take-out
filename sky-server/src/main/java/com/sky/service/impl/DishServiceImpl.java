package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Transactional(rollbackFor = Exception.class)
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

        // 清理缓存数据
        String key = "dish_" + dish.getCategoryId();
        cleanCache(key);
    }

    /**
     * 菜品分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> ids) {
        // 记录菜品的分类id
        Set<Long> categoryIds = new HashSet<>();

        // 判断当前菜品是否起售状态
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            categoryIds.add(dish.getCategoryId());
        }

        // 判断当前菜品是否关联套餐
        for (Long id : ids) {
            Long count = setmealDishMapper.countByDishId(id);
            if (count > 0) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }

        // 删除菜品和口味
        dishMapper.delete(ids);
        dishFlavorMapper.deleteByDishIds(ids);

        // 清理缓存数据
        for (Long cid : categoryIds) {
            String key = "dish_" + cid;
            cleanCache(key);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void startOrStop(Integer status, Long id) {
        // 根据id修改菜品状态
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.update(dish);

        // 清理缓存数据
        Dish updatedDish = dishMapper.getById(id);
        String key = "dish_" + updatedDish.getCategoryId();
        cleanCache(key);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();

        // 根据id查询菜品
        Dish dish = dishMapper.getById(id);
        BeanUtils.copyProperties(dish, dishVO);

        // 根据菜品id查询口味
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void update(DishDTO dishDTO) {
        // 获取更新前的菜品信息
        Dish oldDish = dishMapper.getById(dishDTO.getId());

        // 创建 Dish 对象
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品信息
        dishMapper.update(dish);

        // 删除原有的口味数据
        dishFlavorMapper.deleteByDishIds(List.of(dishDTO.getId()));

        // 重新插入口味数据
        List<DishFlavor> dishFlavors = dishDTO.getFlavors();
        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(dishDTO.getId()));
            dishFlavorMapper.insertBatch(dishFlavors);
        }

        // 清理缓存数据
        String oldKey = "dish_" + oldDish.getCategoryId();
        cleanCache(oldKey);
        String newKey = "dish_" + dish.getCategoryId();
        if (oldKey.equals(newKey)) {
            cleanCache(newKey);
        }

    }

    /**
     * 根据分类条件查询菜品列表
     * @param categoryId
     * @return
     */
    @Override
    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        return dishMapper.list(dish);
    }

    /**
     * 条件查询菜品和口味
     * @param categoryId
     * @return
     */
    public List<DishVO> listWithFlavor(long categoryId) {
        // 1.构造redis中的key
        String key = "dish_" + categoryId;

        // 2.从redis中查询缓存数据, 如果缓存命中，直接返回
        List<DishVO> cache = (List<DishVO>) redisTemplate.opsForValue().get(key);
        if (cache != null && !cache.isEmpty()) {
            return cache;
        }

        // 3.如果缓存未命中，查询数据库
        // 构建查询条件对象
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();
        List<Dish> dishList = dishMapper.list(dish);

        // 创建 DishVO 对象
        List<DishVO> dishVOList = new ArrayList<>();

        // 遍历菜品列表，查询每个菜品对应口味
        for (Dish d : dishList) {
            DishVO dishVO = new DishVO();
            BeanUtils.copyProperties(d,dishVO);

            //根据菜品id查询对应的口味
            List<DishFlavor> flavors = dishFlavorMapper.getByDishId(d.getId());

            dishVO.setFlavors(flavors);
            dishVOList.add(dishVO);
        }

        // 4.将查询结果缓存到redis中
        redisTemplate.opsForValue().set(key, dishVOList,30, TimeUnit.MINUTES);

        return dishVOList;
    }

    /**
     * 清理缓存数据
     * @param pattern
     */
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys(pattern);
        redisTemplate.delete(keys);
    }
}
