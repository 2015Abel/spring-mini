package com.abel.demo.workspace.config;

import com.abel.demo.annotation.Bean;
import com.abel.demo.annotation.Configuration;
import com.abel.demo.workspace.service.ZhangSan;

/**
 * @description: TODO 类描述
 * @author: liuzijian
 * @create: 2018-11-11 21:23
 **/
@Configuration
public class PersonConfig {

    @Bean(value = "zhangsan2")
    public ZhangSan initZhangSan(){
        return new ZhangSan("zhangsan2");
    }
}
