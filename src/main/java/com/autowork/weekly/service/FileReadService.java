package com.autowork.weekly.service;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.Field;
import java.util.*;

@Service
public class FileReadService {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MyClassLoader myClassLoader;

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public  Set<Class<?>> findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes)throws Exception {
        File dir = new File(packagePath);
        if (!dir.exists() || !dir.isDirectory()) {
            return null;
        }
        File[] dirfiles = dir.listFiles(new FileFilter() {
            // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
            public boolean accept(File file) {
                return (recursive && file.isDirectory())
                        || (file.getName().endsWith(".class"));
            }
        });
        // 循环所有文件
        for (File file : dirfiles) {
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                // 添加到集合中去 但是addXXX或者updateXXX不加载
                if(!className.startsWith("add")&& !className.startsWith("update")){
                    if(StringUtils.isNotBlank(packageName)){
                        classes.add(this.myClassLoader.findClass(packageName+"."+className));
                    }else{
                        classes.add(this.myClassLoader.findClass(className));
                    }
                }
            }
        }
        return classes;
    }
    private String classNameToPath(String name) {
        return   name.replace(".", "/");
    }
    /**
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     * @return
     */
    public Map<String,String> findClassAndToMap(String packageName,
                                     String packagePath, final boolean recursive, Set<Class<?>> classes) throws Exception{
        Map<String,String> result = new HashMap<String,String>();
        Set<Class<?>> set = this.findAndAddClassesInPackageByFile(packageName,packagePath,recursive,classes);
        for(Class obj:set){
            Field[] field = obj.getDeclaredFields();
            List<Object> list  = new ArrayList<>();
            for (int i = 0; i < field.length; i++) {
                try{
                    field[i].setAccessible(true);
                    list.add(field[i].getName());
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
            }
            result.put(obj.getName(), JSON.toJSONString(list));
        }
        return result;
    }
}
