package com.classroom.config;

import com.classroom.interceptor.AdminPermissionInterceptor;
import com.classroom.interceptor.LoginInterceptor;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置：Controller 扫描、拦截器、静态资源、文件上传。
 */
@Configuration
@EnableWebMvc
@ComponentScan("com.classroom.controller")
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UploadProperties uploadProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 登录拦截：前台公开接口与登录/注册不拦截，其余 /api/** 需登录
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/api/**")
                .excludePathPatterns(
                        "/api/auth/login",
                        "/api/auth/register",
                        "/api/home",
                        "/api/categories",
                        "/api/courses/**",
                        "/api/chapter-files/**"
                );
        // 后台接口角色权限拦截：仅管理员 / 老师可访问，权限管理类接口仅管理员
        registry.addInterceptor(new AdminPermissionInterceptor())
                .addPathPatterns("/api/admin/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
        // 上传文件静态访问：/upload/** → 本地 upload.path 目录
        String uploadPath = uploadProperties.getPath();
        if (uploadPath != null && !uploadPath.isEmpty()) {
            if (!uploadPath.endsWith("/") && !uploadPath.endsWith("\\")) {
                uploadPath = uploadPath + "/";
            }
            registry.addResourceHandler("/upload/**")
                    .addResourceLocations("file:" + uploadPath);
        }
    }

    /** 文件上传解析：单个/总大小上限 200MB */
    @Bean
    public MultipartConfigElement multipartConfigElement() {
        return new MultipartConfigElement("", 200 * 1024 * 1024, 200 * 1024 * 1024, 0);
    }

    @Bean
    public MultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
}
