package com.GestionPanne.GestionPanne.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

    @Bean
    public WebMvcConfigurer webMvcConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // Autorise toutes les origines
                registry.addMapping("/**")
                        .allowedMethods("GET", "POST", "PUT", "DELETE")
                        .allowedOrigins("*") // Permet l'accès depuis n'importe quel domaine
                        .allowedHeaders("*") // Autorise tous les en-têtes
                        .exposedHeaders("Authorization") // Autorise l'en-tête Authorization pour JWT
                        .allowCredentials(false); // Désactiver si aucune authentification basée sur les cookies n'est nécessaire
            }
        };
    }
}
