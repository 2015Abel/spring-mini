package com.abel.demo.workspace.service;

import com.abel.demo.annotation.Service;
import com.abel.demo.util.StringUtil;

/**
 * @description: ZhangSan
 * @author: liuzijian
 * @create: 2018-11-11 00:03
 **/
@Service
public class ZhangSan implements Person {

    private String name;

    public ZhangSan(String name) {
        this.name = name;
    }

    public ZhangSan() {}

    public String name() {
        if(StringUtil.isEmpty(name)){
            return "zhangsan";
        }else{
            return name;
        }
    }
}
