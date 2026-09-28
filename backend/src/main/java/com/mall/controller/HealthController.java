package com.mall.controller;

import com.mall.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * 系统健康检查接口：验证服务启动、统一返回结构与接口文档是否正常。
 */
@Tag(name = "系统")
@RestController
@RequestMapping("/api/system")
public class HealthController {

    @Operation(summary = "服务健康检查")
    @GetMapping("/ping")
    public Result<Map<String, Object>> ping() {
        return Result.ok(Map.of(
                "service", "mall-backend",
                "status", "UP",
                "time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        ));
    }
}
