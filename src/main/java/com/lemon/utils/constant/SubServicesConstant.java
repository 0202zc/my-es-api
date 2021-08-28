package com.lemon.utils.constant;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Liang Zhancheng
 * @date 2021/8/3 22:16
 * 子服务名称常量类
 */
public class SubServicesConstant {
    public static final String SUB_SERVICE_WEIBO_HOT_SEARCH = "weibo/hot-search";
    public static final String SUB_SERVICE_WEIBO_HOT_KEYWORD = "weibo/hot-keyword";
    public static final String SUB_SERVICE_ZHIHU_TOP_SEARCH = "zhihu/top-search";
    public static final String SUB_SERVICE_ZHIHU_BILLBOARD = "zhihu/billboard";

    /**
     * 获取子服务常量集合
     *
     * @return Set<String> 子服务常量集合
     * @throws IllegalAccessException
     */
    public static Set<String> getSubServicesSet() throws IllegalAccessException {
        Class<SubServicesConstant> clazz = SubServicesConstant.class;
        Field[] fields = clazz.getFields();
        Set<String> set = new HashSet<>(fields.length);
        for (Field field : fields) {
            set.add(field.get(clazz).toString());
        }
        return set;
    }

    /**
     * 获取子服务常量映射
     *
     * @return Map<String, String> 子服务常量映射
     * @throws IllegalAccessException
     */
    public static Map<String, String> getSubServicesMap() throws IllegalAccessException {
        Class<SubServicesConstant> clazz = SubServicesConstant.class;
        Field[] fields = clazz.getFields();

        Map<String, String> map = new HashMap<>();
        for (Field field : fields) {
            map.put(field.getName(), field.get(clazz).toString());
        }

        return map;
    }
}
