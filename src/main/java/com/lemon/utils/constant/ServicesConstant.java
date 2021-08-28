package com.lemon.utils.constant;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Liang Zhancheng
 * @date 2021/8/3 22:07
 * 服务名称常量类
 */
public class ServicesConstant {
    public static final String SERVICE_WEIBO = "weibo";
    public static final String SERVICE_ZHIHU = "zhihu";

    /**
     * 获取服务常量集合
     * @return Set<String> 服务常量集合
     * @throws IllegalAccessException
     */
    public static Set<String> getServicesSet() throws IllegalAccessException {
        Class<ServicesConstant> clazz = ServicesConstant.class;
        Field[] fields = clazz.getFields();
        Set<String> set = new HashSet<>(fields.length);
        for (Field field : fields) {
            set.add(field.get(clazz).toString());
        }
        return set;
    }

    /**
     * 获取服务常量映射
     * @return Map<String, String> 服务常量映射
     * @throws IllegalAccessException
     */
    public static Map<String, String> getServicesMap() throws IllegalAccessException {
        Class<ServicesConstant> clazz = ServicesConstant.class;
        Field[] fields = clazz.getFields();

        Map<String, String> map = new HashMap<>();
        for (Field field : fields) {
            map.put(field.getName(), field.get(clazz).toString());
        }

        return map;
    }
}
