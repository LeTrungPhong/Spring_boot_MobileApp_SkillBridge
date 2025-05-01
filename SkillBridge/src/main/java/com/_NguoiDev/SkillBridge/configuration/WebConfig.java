//package com._NguoiDev.SkillBridge.configuration;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.CorsRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class WebConfig implements WebMvcConfigurer {
//    @Override
//    public void addCorsMappings(CorsRegistry registry) {
//        WebMvcConfigurer.super.addCorsMappings(registry);
//        registry.addMapping("/**")  // Áp dụng cho tất cả các endpoint
//                .allowedOrigins("http://192.168.1.4:8081", "http://localhost:8081")  // Liệt kê các origin cụ thể
//                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
//                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With")
//                .allowCredentials(true);  // Bật cookies và credentials
//    }
//}
