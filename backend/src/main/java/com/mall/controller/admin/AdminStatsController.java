package com.mall.controller.admin;

import com.mall.common.Result;
import com.mall.entity.vo.StatsVO;
import com.mall.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端统计接口（需 ROLE_ADMIN）。
 */
@Tag(name = "管理端-统计")
@RestController
@RequestMapping("/api/admin")
public class AdminStatsController {

    private final StatsService statsService;

    public AdminStatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @Operation(summary = "总览统计（商品/分类/订单/销售额 + 近7日趋势）")
    @GetMapping("/stats")
    public Result<StatsVO> stats() {
        return Result.ok(statsService.getAdminStats());
    }
}
