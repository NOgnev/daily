package com.klaxon.diary.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Перенаправление всех запросов на index.html
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/index.html");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Обработка статических ресурсов
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/");
    }

    // Добавление обработчика ошибок для перенаправления на index.html
    @Override
    public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add((request, response, handler, ex) -> {
            if (ex instanceof NoHandlerFoundException) {
                return new ModelAndView("forward:/index.html");
            }
            if (ex instanceof NoResourceFoundException) {
                return new ModelAndView("forward:/index.html");
            }
            return null;
        });
    }
}
