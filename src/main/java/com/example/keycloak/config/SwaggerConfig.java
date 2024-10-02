package com.example.keycloak.config;

 import io.swagger.v3.oas.models.Components;
 import io.swagger.v3.oas.models.OpenAPI;
 import io.swagger.v3.oas.models.info.Info;
 import io.swagger.v3.oas.models.info.License;
 import io.swagger.v3.oas.models.security.SecurityRequirement;
 import io.swagger.v3.oas.models.security.SecurityScheme;
 import com.example.keycloak.response.SwaggerConstant;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 @Configuration
 public class SwaggerConfig {

 	@Bean
 	public OpenAPI customOpenAPI(){

 		License mitLicense = new License().name("Apache 2.0").url("http://springdoc.org");
 		Info info = new Info().title("keycloak").version("1.0")
 				.description("keycloak Backend service End points")
 				.termsOfService("http://swagger.io/terms/")
				.license(mitLicense);
 		return new OpenAPI().info(info)
 			.addSecurityItem(new SecurityRequirement()
 				.addList(SwaggerConstant.AUTHENTICATION)).components(new Components().addSecuritySchemes(SwaggerConstant.AUTHENTICATION, new SecurityScheme()
 					.name(SwaggerConstant.AUTHENTICATION)
 					.type(SecurityScheme.Type.HTTP)
					.scheme("bearer").bearerFormat("JWT")));
	}

 }

