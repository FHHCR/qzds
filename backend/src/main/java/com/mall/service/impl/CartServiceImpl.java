package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mall.common.BusinessException;
import com.mall.entity.dto.CartAddDTO;
import com.mall.entity.po.Cart;
import com.mall.entity.po.Product;
import com.mall.entity.vo.CartItemVO;
import com.mall.mapper.CartMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.CartService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物车服务实现。
 */
@Service
public class CartServiceImpl implements CartService {

    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    public CartServiceImpl(CartMapper cartMapper, ProductMapper productMapper) {
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
    }

    @Override
    public void add(Long userId, CartAddDTO req) {
        Product product = productMapper.selectById(req.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        Cart cart = cartMapper.selectOne(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, req.getProductId()));
        int newQuantity = req.getQuantity() + (cart == null ? 0 : cart.getQuantity());
        if (newQuantity > product.getStock()) {
            throw new BusinessException(400, "库存不足，当前库存 " + product.getStock() + " 件");
        }
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(req.getProductId());
            cart.setQuantity(req.getQuantity());
            cartMapper.insert(cart);
        } else {
            cart.setQuantity(newQuantity);
            cartMapper.updateById(cart);
        }
    }

    @Override
    public List<CartItemVO> list(Long userId) {
        List<Cart> carts = cartMapper.selectList(new LambdaQueryWrapper<Cart>()
                .eq(Cart::getUserId, userId)
                .orderByDesc(Cart::getId));
        if (carts.isEmpty()) {
            return List.of();
        }
        List<Long> productIds = carts.stream().map(Cart::getProductId).toList();
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        return carts.stream().map(cart -> {
            CartItemVO vo = new CartItemVO();
            vo.setId(cart.getId());
            vo.setProductId(cart.getProductId());
            vo.setQuantity(cart.getQuantity());
            Product p = productMap.get(cart.getProductId());
            if (p != null) {
                vo.setName(p.getName());
                vo.setMainImage(p.getMainImage());
                vo.setPrice(p.getPrice());
                vo.setStock(p.getStock());
            }
            return vo;
        }).toList();
    }

    @Override
    public void updateQuantity(Long userId, Long id, Integer quantity) {
        Cart cart = getOwnedCart(userId, id);
        Product product = productMapper.selectById(cart.getProductId());
        if (product == null || product.getStatus() == null || product.getStatus() != 1) {
            throw new BusinessException(400, "商品已下架");
        }
        if (quantity > product.getStock()) {
            throw new BusinessException(400, "库存不足，当前库存 " + product.getStock() + " 件");
        }
        cart.setQuantity(quantity);
        cartMapper.updateById(cart);
    }

    @Override
    public void remove(Long userId, Long id) {
        cartMapper.deleteById(getOwnedCart(userId, id).getId());
    }

    private Cart getOwnedCart(Long userId, Long id) {
        Cart cart = cartMapper.selectById(id);
        if (cart == null || !cart.getUserId().equals(userId)) {
            throw new BusinessException(404, "购物车条目不存在");
        }
        return cart;
    }
}
