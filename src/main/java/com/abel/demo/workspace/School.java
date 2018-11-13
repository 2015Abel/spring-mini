package com.abel.demo.workspace;

import com.abel.demo.annotation.Compent;
import com.abel.demo.annotation.Resource;
import com.abel.demo.workspace.service.Person;

/**
 * @description: School
 * @author: liuzijian
 * @create: 2018-11-11 00:04
 **/
@Compent("happySchool")
public class School {

    @Resource
    private Person person;

    @Resource(name = "zhangsan2")
    private Person person2;



}
