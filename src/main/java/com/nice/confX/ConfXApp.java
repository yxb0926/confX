package com.nice.confX;

import com.nice.confX.model.User;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by yxb on 16/6/27.
 */

@EnableTransactionManagement
@SpringBootApplication
public class ConfXApp extends SpringBootServletInitializer {
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ConfXApp.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(ConfXApp.class, args);
    }

    @Configuration
    static class WebMvcConfigurer extends WebMvcConfigurerAdapter {

        public void addInterceptors(final InterceptorRegistry registry) {
            registry.addInterceptor(new HandlerInterceptorAdapter() {

                @Override
                public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                                         Object handler) throws Exception {
                    request.getContextPath();

                    String loginUrl = "/manager/project/login";
                    User sessionUser = (User) request.getSession().getAttribute("sessionUser");

                    if(request.getServletPath().startsWith(loginUrl)) {
                        return true;
                    } else if (sessionUser != null){
                        return true;
                    } else {
                        response.sendRedirect(loginUrl);
                        return false;
                    }
                }
            }).addPathPatterns("/manager/**");
        }
    }
}

