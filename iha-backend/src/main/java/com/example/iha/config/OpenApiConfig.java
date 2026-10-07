package com.example.iha.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI ihaOpenApi() {
        return new OpenAPI().info(new Info()
                .title("İHA Kayıt Yönetim Sistemi API")
                .description("İnsansız hava araçlarının kayıt, durum ve bakım bilgilerini yöneten REST API")
                .version("v1"));
    }
}
