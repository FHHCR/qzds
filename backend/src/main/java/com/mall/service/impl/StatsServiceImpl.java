package com.mall.service.impl;

import com.mall.entity.vo.StatsVO;
import com.mall.mapper.CategoryMapper;
import com.mall.mapper.OrderMapper;
import com.mall.mapper.ProductMapper;
import com.mall.service.StatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 管理端统计服务实现。
 */
@Service
public class StatsServiceImpl implements StatsService {

    private final ProductMapper productMapper;
    private final CategoryMapper categoryMapper;
    private final OrderMapper orderMapper;

    public StatsServiceImpl(ProductMapper productMapper,
                            CategoryMapper categoryMapper,
                            OrderMapper orderMapper) {
        this.productMapper = productMapper;
        this.categoryMapper = categoryMapper;
        this.orderMapper = orderMapper;
    }

    @Override
    public StatsVO getAdminStats() {
        StatsVO vo = new StatsVO();
        vo.setProductCount(productMapper.selectCount(null));
        vo.setCategoryCount(categoryMapper.selectCount(null));
        vo.setOrderCount(orderMapper.selectCount(null));
        vo.setSalesAmount(orderMapper.selectPaidAmount());
        // 近 7 日（含今天）：从 6 天前 0 点开始
        List<StatsVO.TrendItem> trend = orderMapper.selectTrend(
                LocalDateTime.now().minusDays(6).withHour(0).withMinute(0).withSecond(0).withNano(0));
        vo.setTrend(trend == null ? List.of() : trend);
        return vo;
    }
}
