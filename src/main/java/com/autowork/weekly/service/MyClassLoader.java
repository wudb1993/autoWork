package com.autowork.weekly.service;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;

@Service
public class MyClassLoader extends ClassLoader {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${path.class}")
    private String pathPackageName;



    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        // 父类已加载
        //this.findLoadedClass(name);
        Class clazz = null;
        //根据类的二进制名称,获得该class文件的字节码数组
        byte[] classData = getClassData(pathPackageName+classNameToPath("."+name));
        if (classData == null) {
            throw new ClassNotFoundException();
        }
        //将class的字节码数组转换成Class类的实例
        clazz = defineClass(classNameFormat(name), classData, 0, classData.length);

        return clazz;
    }

    private byte[] getClassData(String name) {
        InputStream is = null;
        try {
            String path = name+".class";
            log.info(path);
            FileInputStream url = new FileInputStream(path);
            byte[] buff = new byte[1024*4];
            int len = -1;
            is = url;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while((len = is.read(buff)) != -1) {
                baos.write(buff,0,len);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //注意运行环境是linux还是windows 文件路径写法不一样
    private String classNameToPath(String name) {
        return   name.replace(".", "/") ;
    }

    private String classNameFormat(String name) {
        String result = name.replace("\\", ".").replace("/",".");
        return result;
    }
}
