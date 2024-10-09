package com.nakqeeb.posts_app.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

// refer https://www.baeldung.com/spring-boot-swagger-jwt
@Configuration
public class SwaggerConfiguration {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI().addSecurityItem(new SecurityRequirement().addList("Bearer Authentication"))
                .components(new Components().addSecuritySchemes("Bearer Authentication", createAPIKeyScheme()))
                .info(new Info().title("Posts/Articles")
                        .description("Posts App is a modular Spring Boot application providing user authentication, post management, and admin capabilities. It features:\n" +
                                "\n" +
                                "\t•\tAuth Module: User registration and login endpoints.\n" +
                                "\t•\tPosts Module: Create, update, delete, and view posts. Authenticated and non-authenticated users can access approved posts.\n" +
                                "\t•\tAdmin Module: Manage user roles, activate accounts, approve posts, fetch user/post information, and track login statistics.\n" +
                                "\t•\tIt supports features like comments, post analytics, and a like system to enhance user engagement.\n" +
                                "\t•\tThe app ensures each user can like a post only once, they also have the option to remove their like (unlike) if desired and tracks post views and likes to offer detailed insights.\n" +
                                "\t \tViewing Count: An aspect is used to implement and track the number of views per post.\n" +
                                "\t•\tLogin Counter: An Aspect is used to log login attempts and record successful and failed logins, offering detailed insights into authentication activities.")
                        .version("1.0").contact(new Contact().name("Nakqeeb").email("nakqeeb@gmail.com").url("https://nakqeeb.github.io/portfolio/"))
                        .license(new License().name("License of API")
                                .url("API license URL")));
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme().type(SecurityScheme.Type.HTTP)
                .bearerFormat("JWT")
                .scheme("bearer");
    }

}