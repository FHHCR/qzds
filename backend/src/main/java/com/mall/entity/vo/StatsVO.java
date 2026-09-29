package com.mall.entity.vo;

import lombok.Data;

import java.util.List;

/**
 * 管理端统计总览 VO。
 */
@Data
public class StatsVO {

    /** 商品总数（不含逻辑删除） */
    private Long productCount;

    /** 分类总数（不含逻辑删除） */
    private Long categoryCount;

    /** 订单总数 */
    private Long orderCount;

    /** 销售总额（已支付订单，单位：分） */
    private Long salesAmount;

    /** 近 7 日订单趋势（按天，含当天） */
    private List<TrendItem> trend;

    /** 单日趋势项 */
    @Data
    public static class TrendItem {
        /** 日期 yyyy-MM-dd */
        private String day;

        /** 当日订单数 */
        private Long count;

        /** 当日销售额（已支付订单，单位：分） */
        private Long amount;
    }
}
