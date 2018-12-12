package com.massestech;

import com.massestech.common.mybatis.mapper.BaseMapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(value = "com.massestech", markerInterface = BaseMapper.class)
@SpringBootApplication
public class JavaBaseTemplateApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaBaseTemplateApplication.class, args);
    }

}
