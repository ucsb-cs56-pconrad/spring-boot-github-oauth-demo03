package org.pac4j.demo.spring;

import org.pac4j.core.config.Config;
import org.pac4j.springframework.annotation.AnnotationConfig;
import org.pac4j.springframework.helper.HelperConfig;
import org.pac4j.springframework.web.SecurityInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@Import({HelperConfig.class, AnnotationConfig.class})
@ComponentScan(basePackages = "org.pac4j.springframework.web")
public class SecurityConfig extends WebMvcConfigurerAdapter {

    @Autowired
    private Config config;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SecurityInterceptor(config, "GitHubClient", "admin")).addPathPatterns("/admin/*");
		registry.addInterceptor(new SecurityInterceptor(config, "GitHubClient", "custom")).addPathPatterns("/custom/*");
        registry.addInterceptor(new SecurityInterceptor(config, "GitHubClient")).addPathPatterns("/github/*");
        registry.addInterceptor(new SecurityInterceptor(config)).addPathPatterns("/protected/*");
    }
}
