package com.lunghealth.monitoring.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI lungHealthOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Lung Health Monitoring System API")
                        .description("Clinical Respiratory Health Telemetry, Symptom Analysis, Lung Function (Spirometry ATS/ERS) Monitoring, Multi-Factor Risk Stratification, and Automated Health Report Generation.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Pulmonary Informatics & Clinical AI Team")
                                .email("clinical@lunghealth.local"))
                        .license(new License().name("Apache 2.0")));
    }
}
