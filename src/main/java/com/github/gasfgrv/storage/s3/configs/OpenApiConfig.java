package com.github.gasfgrv.storage.s3.configs;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(
        title = "File Storage api S3",
        description = "Api para fazer upload/download de arquivos no S3",
        version = "V1",
        contact = @Contact(
                name = "gasfgrv",
                email = "gustavo_almeida11@hotmail.com",
                url = "https://github.com/gasfgrv")
))
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi logApi() {
        return GroupedOpenApi.builder()
                .group("api-v1")
                .packagesToScan("com.github.gasfgrv.storage.s3.controller")
                .pathsToMatch("/v1/**")
                .build();
    }

}
