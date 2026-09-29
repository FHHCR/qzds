package com.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mall.entity.po.Order;
import com.mall.entity.vo.StatsVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单 Mapper。
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /** 已支付订单销售总额（单位：分） */
    @Select("SELECT COALESCE(SUM(total_price), 0) FROM `order` WHERE status = 1")
    Long selectPaidAmount();

    /** 指定起始时间以来的每日订单数与销售额（按天分组，升序） */
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') AS day, COUNT(*) AS count, "
            + "COALESCE(SUM(CASE WHEN status = 1 THEN total_price ELSE 0 END), 0) AS amount "
            + "FROM `order` WHERE create_time >= #{start} "
            + "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d') ORDER BY day")
    List<StatsVO.TrendItem> selectTrend(@Param("start") LocalDateTime start);
}
