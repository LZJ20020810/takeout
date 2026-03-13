package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetmealMapper setmealMapper;
    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    @Override
    public void addShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        System.out.println("111:"+shoppingCart);

        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        System.out.println("list:"+list);

        //如果已经存在了，则数量加一
        if(list!=null&&list.size()>0){
            ShoppingCart cart = list.get(0);
            System.out.println("shoppingCart:"+shoppingCart);
            cart.setNumber(cart.getNumber()+1);
            System.out.println("cart:"+cart);

            shoppingCartMapper.updateNumberById(cart);
        }else{
            //若是不存在，则需要插入一条购物车数据
            Long dishId = shoppingCartDTO.getDishId();
            if(dishId!=null){//是菜品
                Dish dish = dishMapper.getById(dishId);
                shoppingCart.setName(dish.getName());
                shoppingCart.setImage(dish.getImage());
                shoppingCart.setAmount(dish.getPrice());
                shoppingCart.setNumber(1);
                shoppingCart.setCreateTime(LocalDateTime.now());

            }else{//是套餐
                Long setmealId = shoppingCartDTO.getSetmealId();
                if(setmealId!=null){
                    Setmeal setmeal = setmealMapper.getById(setmealId);
                    shoppingCart.setName(setmeal.getName());
                    shoppingCart.setImage(setmeal.getImage());
                    shoppingCart.setNumber(1);
                    shoppingCart.setAmount(setmeal.getPrice());
                    shoppingCart.setCreateTime(LocalDateTime.now());
                }

                shoppingCartMapper.insert(shoppingCart);
            }
        }




    }

    /**
     * 查看购物车
     * @return
     */
    @Override
    public List<ShoppingCart> showShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        ShoppingCart shoppingCart = ShoppingCart.builder()
                                                 .userId(userId)
                                                  .build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        return list;
    }

    /**
     * 清空购物车
     */
    @Override
    public void cleanShoppingCart() {
        Long userId = BaseContext.getCurrentId();
        shoppingCartMapper.clean(userId);
    }

    /**
     * 减少购物车
     */
    @Override
    public void subShoppingCart(ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO,shoppingCart);
        System.out.println("111:"+shoppingCart);

        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);

        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        System.out.println("list:"+list);

        //如果已经存在了，则数量减一
        if(list!=null&&list.size()>0){
            ShoppingCart cart = list.get(0);
            System.out.println("shoppingCart:"+shoppingCart);
            if(cart.getNumber()-1==0){
                //删除这条数据
                shoppingCartMapper.deleteShoppingCart(shoppingCart);
            }else{
                //更改数量信息
                cart.setNumber(cart.getNumber()-1);
                shoppingCartMapper.updateNumberById(cart);
            }
            System.out.println("cart:"+cart);


        }
    }
}
