package com.yakubovskiy.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@Configuration
public class SwaggerConfig {
    ApiInfo apiInfo() {
        return new ApiInfoBuilder().title("Computer equipment online-store")
                .description("Project for passage in NetCracker")
                .license("No license").licenseUrl("http://unlicense.org").termsOfServiceUrl("").version("1.0")
                .contact(new Contact("Aleksey Yakubovskiy", "https://t.me/aleksey2411", "aleksey24112@gmail.com")).build();
    }

    @Bean
    public Docket docket() {
        List<SecurityReference> references = Collections.singletonList(new SecurityReference("basicAuth",
                Stream.of(new AuthorizationScope("basicAuth", "basicAuth"))
                        .toArray(AuthorizationScope[]::new)));
        List<SecurityContext> securityContexts = Collections
                .singletonList(SecurityContext.builder().securityReferences(references).build());
        return new Docket(DocumentationType.SWAGGER_2)
                .securitySchemes(Arrays.asList(new BasicAuth("basicAuth")))
                .securityContexts(securityContexts)
                .apiInfo(apiInfo())
                .select()
                .paths(PathSelectors.any())
                .apis(RequestHandlerSelectors.basePackage("com.yakubovskiy.project.controller"))
                .build();
    }

}
