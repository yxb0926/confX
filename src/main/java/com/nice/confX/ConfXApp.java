package com.nice.confX;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * Created by yxb on 16/6/27.
 */

@SpringBootApplication
public class ConfXApp extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ConfXApp.class);
    }
    public static void main(String[] args) throws Exception{
        SpringApplication.run(ConfXApp.class, args);
    }
}

