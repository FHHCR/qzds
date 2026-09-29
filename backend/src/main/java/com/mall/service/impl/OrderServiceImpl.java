package com.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mall.common.BusinessException;
import com.mall.common.PageVO;
import com.mall.entity.dto.AdminOrderQueryDTO;
import com.mall.entity.dto.CreateOrderDTO;
import com.mall.entity.po.Address;
import com.mall.entity.po.Cart;
import com.mall.entity.po.Order;
import com.mall.entity.po.OrderItem;
import com.mall.entity.po.Product;
import com.mall.entity.vo.OrderDetailVO;
import com.mall.entity.vo.OrderVO;
import com.mall.mapper.AddressMapper;
import com.mall.mapper.CartMapper;
import com.mall.mapper.OrderItemMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 订单服务实现。
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;
    private final AddressMapper addressMapper;

    public OrderServiceImpl(OrderMapper orderMapper, OrderItemMapper orderItemMapper,
                            CartMapper cartMapper, ProductMapper productMapper,
                            AddressMapper addressMapper) {
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.cartMapper = cartMapper;
        this.productMapper = productMapper;
        this.addressMapper = addressMapper;
    }

    @Override
    @Transactional
    public OrderVO createOrder(Long userId, CreateOrderDTO req) {
        List<Long> cartIds = req.getCartIds().stream().distinct().toList();
        List<Cart> carts = cartMapper.selectBatchIds(cartIds);
        if (carts.size() != cartIds.size()) {
            throw new BusinessException(400, "部分购物车条目不存在");
        }
        for (Cart cart : carts) {
            if (!cart.getUserId().equals(userId)) {
                throw new BusinessException(400, "部分购物车条目不存在");
            }
        }

        // 校验收货地址归属，并固化快照
        Address address = addressMapper.selectById(req.getAddressId());
        if (address == null || !address.getUserId().equals(userId)) {
            throw new BusinessException(400, "收货地址不存在");
        }
        String addressSnapshot = (address.getReceiver() + " " + address.getPhone() + " "
                + address.getRegion() + " " + address.getDetail()).trim();

        // 校验商品与库存，计算总价
        List<Long> productIds = carts.stream().map(Cart::getProductId).toList();
        Map<Long, Product> productMap = productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(Product::getId, p -> p));
        long total = 0;
        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId());
            if (product == null || product.getStatus() == null || product.getStatus() != 1) {
                throw new BusinessException(400, "商品已下架或不存在");
            }
            if (cart.getQuantity() > product.getStock()) {
                throw new BusinessException(400, "「" + product.getName() + "」库存不足");
            }
            total += product.getPrice() * cart.getQuantity();
        }

        // 创建订单
        Order order = new Order();
        order.setOrderNo(genOrderNo());
        order.setUserId(userId);
        order.setTotalPrice(total);
        order.setAddressSnapshot(addressSnapshot);
        order.setStatus(0);
        orderMapper.insert(order);

        // 写明细（快照）并扣减库存
        for (Cart cart : carts) {
            Product product = productMap.get(cart.getProductId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getMainImage());
            item.setPrice(product.getPrice());
            item.setQuantity(cart.getQuantity());
            orderItemMapper.insert(item);

            product.setStock(product.getStock() - cart.getQuantity());
            productMapper.updateById(product);
        }

        // 清空已结算的购物车条目
        cartMapper.deleteBatchIds(cartIds);

        OrderVO vo = new OrderVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setStatus(order.getStatus());
        vo.setItemCount(carts.stream().mapToInt(Cart::getQuantity).sum());
        vo.setCreateTime(order.getCreateTime());
        return vo;
    }

    @Override
    public List<OrderVO> listOrders(Long userId) {
        List<Order> orders = orderMapper.selectList(new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime)
                .orderByDesc(Order::getId));
        if (orders.isEmpty()) {
            return List.of();
        }
        // 统计每个订单的商品件数
        List<Long> orderIds = orders.stream().map(Order::getId).toList();
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .in(OrderItem::getOrderId, orderIds));
        Map<Long, Integer> countMap = items.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderId,
                        Collectors.summingInt(OrderItem::getQuantity)));
        return orders.stream().map(order -> {
            OrderVO vo = new OrderVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
            vo.setTotalPrice(order.getTotalPrice());
            vo.setAddressSnapshot(order.getAddressSnapshot());
            vo.setStatus(order.getStatus());
            vo.setItemCount(countMap.getOrDefault(order.getId(), 0));
            vo.setCreateTime(order.getCreateTime());
            return vo;
        }).toList();
    }

    @Override
    public OrderDetailVO getOrderDetail(Long userId, Long id) {
        Order order = getOwnedOrder(userId, id);
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .orderByAsc(OrderItem::getId)));
        return vo;
    }

    @Override
    @Transactional
    public void pay(Long userId, Long id) {
        Order order = getOwnedOrder(userId, id);
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "当前状态不可支付");
        }
        order.setStatus(1);
        orderMapper.updateById(order);
    }

    @Override
    @Transactional
    public void cancel(Long userId, Long id) {
        Order order = getOwnedOrder(userId, id);
        if (order.getStatus() != 0) {
            throw new BusinessException(400, "当前状态不可取消");
        }
        // 恢复库存
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId()));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                productMapper.updateById(product);
            }
        }
        order.setStatus(2);
        orderMapper.updateById(order);
    }

    private Order getOwnedOrder(Long userId, Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new BusinessException(404, "订单不存在");
        }
        return order;
    }

    // ==================== 管理端 ====================

    @Override
    public PageVO<OrderVO> adminPage(AdminOrderQueryDTO query) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        if (query.getStatus() != null) {
            wrapper.eq(Order::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Order::getCreateTime).orderByDesc(Order::getId);
        Page<Order> page = orderMapper.selectPage(new Page<>(query.getPage(), query.getSize()), wrapper);

        List<OrderVO> records = List.of();
        List<Order> orders = page.getRecords();
        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream().map(Order::getId).toList();
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .in(OrderItem::getOrderId, orderIds));
            Map<Long, Integer> countMap = items.stream()
                    .collect(Collectors.groupingBy(OrderItem::getOrderId,
                            Collectors.summingInt(OrderItem::getQuantity)));
            records = orders.stream().map(order -> {
                OrderVO vo = new OrderVO();
                vo.setId(order.getId());
                vo.setOrderNo(order.getOrderNo());
                vo.setTotalPrice(order.getTotalPrice());
                vo.setUserId(order.getUserId());
                vo.setAddressSnapshot(order.getAddressSnapshot());
                vo.setStatus(order.getStatus());
                vo.setItemCount(countMap.getOrDefault(order.getId(), 0));
                vo.setCreateTime(order.getCreateTime());
                return vo;
            }).toList();
        }
        PageVO<OrderVO> pv = new PageVO<>();
        pv.setRecords(records);
        pv.setTotal(page.getTotal());
        pv.setPage(page.getCurrent());
        pv.setSize(page.getSize());
        return pv;
    }

    @Override
    public OrderDetailVO adminGetDetail(Long id) {
        Order order = orderMapper.selectById(id);
        if (order == null) {
            throw new BusinessException(404, "订单不存在");
        }
        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalPrice(order.getTotalPrice());
        vo.setAddressSnapshot(order.getAddressSnapshot());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());
        vo.setItems(orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, order.getId())
                .orderByAsc(OrderItem::getId)));
        return vo;
    }

    /** 订单号：yyyyMMddHHmmss + 6 位随机数 */
    private String genOrderNo() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + String.format("%06d", ThreadLocalRandom.current().nextInt(1000000));
    }
}
