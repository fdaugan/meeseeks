/*
 * Licensed under MIT (https://github.com/ligoj/ligoj/blob/master/LICENSE)
 */
package org.fabdouglas.meeseeks;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any())
				.paths(PathSelectors.any()).build().apiInfo(apiInfo());
	}

	private ApiInfo apiInfo() {
		ApiInfo apiInfo = new ApiInfo("DevOps Material Spring",
				"A Spring Boot application designed to test a cluster, mocking memory, cpu, auto-kill, etc. behaviors",
				"API TOS", "Terms of service",
				new Contact("Fabrice Daugan", "https://ligoj.github.io/ligoj/", "fabrice.daugan@gmail.com"), "MIT",
				"http://fabdouglas.mit-license.org/", Collections.emptyList());
		return apiInfo;
	}
}