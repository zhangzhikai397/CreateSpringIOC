package com.springframework;

import com.springframework.annotation.Autowired;
import com.springframework.annotation.Component;
import com.springframework.service.AService;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

/**
 * Hello world!
 *
 */
public class App {

    /**
     * 使用Map结构保存对象上下文
     */
    private static ConcurrentHashMap<String, Object> context = new ConcurrentHashMap<String, Object>();

    public static void main( String[] args ) {
        // System.out.println( "Hello World!" );

        try {
            // 获取当前启动类路径，扫描启动类路径下所有的class文件
            String filePath = App.class.getResource("/").getPath();
            File file = new File(URLDecoder.decode(filePath, "utf-8"));
            System.out.println(file);

            // 初始化容器
            recursionFile(file, "");
            // 输出纳入容器管理的对象
            context.forEach((k, v) -> {
                System.out.println(k + " -> " + v.getClass().getName());
            });

            // 相互set
            interSet();
            System.out.println("开始测试");

            AService aService = (AService) context.get("AServiceImpl");
            System.out.println(aService.a());


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static void interSet() {
        // 相互set过程
        context.forEach((k, v) -> {
            Stream.of(v.getClass().getDeclaredFields()).forEach(field -> {
                if (field.getAnnotation(Autowired.class) != null) {
                    // 根据名字匹配
                    Object obj = context.get(field.getName());
                    if (obj != null) {
                        try {
                            field.setAccessible(true);
                            field.set(v, obj);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // 根据接口 父类匹配
                        context.forEach((k1, v2) -> {
                            if (field.getType().isInstance(v2)) {
                                try {
                                    field.setAccessible(true);
                                    field.set(v, v2);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        });
    }

    /**
     * 递归遍历所有的类
     *
     * @param file
     * @param pac
     */
    public static void recursionFile(final File file, String pac) {
        if (file.isDirectory()) {
            Stream.of(file.list()).forEach(value -> {
                File f = new File(file.getAbsoluteFile() + File.separator + value);
                recursionFile(f, null == pac || pac == "" ? value : pac + "." + value);
            });
        } else {
            if (file.getName().endsWith(".class")) {
                Class clazz = null;
                Object obj = null;
                try {
                    clazz = Class.forName(pac.substring(0, pac.indexOf(".class")));
                    if (clazz.isInterface()) {
                        return; // 接口不能实例化
                    }
                    obj = clazz.newInstance();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                }
                if (null == obj) {
                    return;  // 注释无法实例化
                }
                /**
                 * 以类的全名称为键
                 * 以类的实例为值
                 * put到map结构里面去
                 */
                if (obj.getClass().getAnnotation(Component.class) != null) {
                    context.put(file.getName().substring(0, file.getName().indexOf(".class")), obj);
                }
            }
        }
    }
}
