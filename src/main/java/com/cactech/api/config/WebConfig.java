package com.cactech.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Configuracao MVC central da aplicacao.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.upload.avatar-dir:uploads/avatars}")
    private String avatarDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path uploadPath = Paths.get(avatarDir).toAbsolutePath().normalize();
        registry.addResourceHandler("/api/usuarios/avatar/**")
                .addResourceLocations(uploadPath.toUri().toString());
    }
}