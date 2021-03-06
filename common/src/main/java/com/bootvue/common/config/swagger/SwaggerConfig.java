package com.bootvue.common.config.swagger;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.schema.ScalarType;
import springfox.documentation.service.ParameterType;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.Collections;

@Configuration
@ConditionalOnProperty(value = {"app.swagger"}, havingValue = "true", matchIfMissing = false)
public class SwaggerConfig {

    @Bean(value = "privateAPI")
    public Docket privateAPI() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bootvue"))
                .paths(PathSelectors.regex("^(?!/auth).*"))
                .build().groupName("privateApi")
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .globalRequestParameters(
                        Collections.singletonList(new RequestParameterBuilder()
                                .name("token")
                                .description("token信息")
                                .in(ParameterType.HEADER)
                                .query(q -> q.model(m -> m.scalarModel(ScalarType.STRING)))
                                .required(true)
                                .build()))
                .apiInfo(new ApiInfoBuilder()
                        .title("App接口文档")
                        .version("1.0.0")
                        .build())
                ;
    }

    @Bean(value = "publicApi")
    public Docket publicApi() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.bootvue.controller.authentication"))
                .paths(PathSelectors.any())
                .build().groupName("publicApi")
                .pathMapping("/")
                .genericModelSubstitutes(ResponseEntity.class)
                .useDefaultResponseMessages(false)
                .enableUrlTemplating(false)
                .apiInfo(new ApiInfoBuilder()
                        .title("App接口文档")
                        .version("1.0.0")
                        .build())
                ;
    }

}
