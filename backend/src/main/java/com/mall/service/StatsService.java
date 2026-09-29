package com.mall.service;

import com.mall.entity.vo.StatsVO;

/**
 * 管理端统计服务。
 */
public interface StatsService {

    /** 管理端总览统计：商品/分类/订单/销售额 + 近 7 日趋势 */
    StatsVO getAdminStats();
}
