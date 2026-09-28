package com.mall;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 电商平台后端启动类。
 *
 * <p>当前为 Spring Boot 单体骨架；后续演进为 Spring Cloud 时，
 * 本类保留为各微服务启动类模板，配置迁移至配置中心即可。</p>
 */
@SpringBootApplication
@MapperScan("com.mall.mapper")
public class MallApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallApplication.class, args);
    }
}
