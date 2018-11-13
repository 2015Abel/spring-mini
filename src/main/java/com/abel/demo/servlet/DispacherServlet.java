package com.abel.demo.servlet;

import com.abel.demo.annotation.Bean;
import com.abel.demo.annotation.Compent;
import com.abel.demo.annotation.Configuration;
import com.abel.demo.annotation.Resource;
import com.abel.demo.annotation.Service;
import com.abel.demo.util.StringUtil;
import com.alibaba.fastjson.JSON;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @description: 程序入口
 * @author: liuzijian
 * @create: 2018-11-08 06:35
 **/
public class DispacherServlet extends HttpServlet {

    private ServletConfig servletConfig;
    private Properties propertiesConfig = new Properties();

    //存放扫描到的全部class
    List<String> classNames = new ArrayList<String>(256);

    //存放bean
    Map<String,Object> beanMap = new ConcurrentHashMap<String, Object>();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doGet(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //do something
        System.out.println("this is post method..");
    }

    /**
     * 模拟spring ios容器初始化以及依赖注入过程
     * @throws ServletException
     */
    @Override
    public void init() throws ServletException {
        //定位
        String propertiesPath = doRead();

        //加载
        doLocad(propertiesPath);

        //注册
        try {
            doRegistry();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //依赖注入，将beanMap中的bean初始化——为属性赋值
        doInit();

        System.out.println(JSON.toJSONString(beanMap));

    }

    private void doInit() {
        if(beanMap.size() == 0){
            return;
        }

        for(Map.Entry<String,Object> entry:beanMap.entrySet()){
            Class clz = entry.getValue().getClass();
            Field[] fields = clz.getDeclaredFields();
            for(Field field:fields){
                field.setAccessible(true);
                Resource resource = field.getAnnotation(Resource.class);
                if(resource!=null){
                    if(StringUtil.isEmpty(resource.name())){
                        try {
                            field.set(entry.getValue(),beanMap.get(field.getName()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }else {
                        try {
                            field.set(entry.getValue(),beanMap.get(resource.name()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void doRegistry() throws Exception {
        String scanPackage = propertiesConfig.getProperty("base.package");
        doScan(scanPackage);
        for(String name : classNames){
            Class clz = Class.forName(name);
            Annotation annotation = null;
            String resName = null;
            if((annotation=clz.getAnnotation(Compent.class))!=null){
                Compent compent = (Compent)annotation;
                if(StringUtil.isEmpty(compent.value())){
                    resName = StringUtil.lowerFirstLetter(clz.getName());
                }else {
                    resName = compent.value();
                }
                beanMap.put(resName,clz.newInstance());
            }else if((annotation = clz.getAnnotation(Service.class))!=null){
                Service service = (Service) annotation;
                if(StringUtil.isEmpty(service.value())){
                    Class[] interfaceClz = clz.getInterfaces();
                    resName = StringUtil.lowerFirstLetter(interfaceClz[0].getSimpleName());
                }else {
                    resName = service.value();
                }
                beanMap.put(resName,clz.newInstance());
            }else if((annotation = clz.getAnnotation(Configuration.class))!=null){
                //TODO @Configuration and @Bean 实现
                Method[] methods = clz.getMethods();
                for(Method method : methods){
                    Bean bean = method.getAnnotation(Bean.class);
                    if(bean!=null){
                        Class rtClz = method.getReturnType();
                        if(rtClz==null){
                            throw new RuntimeException("A method annotationed by @Bean must have a return type!");
                        }
                        if(StringUtil.isEmpty(bean.value())){
                            resName = rtClz.getName();
                        }else {
                            resName = bean.value();
                        }

                        beanMap.put(resName,method.invoke(clz.newInstance()));
                    }
                }
            }
        }
    }

    private void doScan(String packageName) throws Exception{
        URL url = getClass().getClassLoader().getResource(packageName.replace(".",File.separator));
        File pack = new File(url.toURI());
        for(File file:pack.listFiles()){
            if(file.isDirectory()){
                doScan(packageName+"."+file.getName());
            }else {
                classNames.add(packageName + "." + file.getName().replace(".class",""));
            }
        }
    }



    private void doLocad(String propertiesPath) {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propertiesPath);
        try {
            propertiesConfig.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(inputStream!=null){
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String doRead() {
        servletConfig = getServletConfig();
        String propertyPath = servletConfig.getInitParameter("propertyPath");
//        System.out.println(propertyPath);
        return propertyPath.replace("classpath:","");

    }
}
