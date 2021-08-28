package com.lemon.utils.constant;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Liang Zhancheng
 * @date 2021/8/1 16:17
 * 索引常量类
 */
public class IndicesConstant {
    public static final String INDEX_WEIBO_HOT_SEARCH = "weibo_hot_search";
    public static final String INDEX_ZHIHU_TOP_SEARCH = "zhihu_top_search";
    public static final String INDEX_ZHIHU_BILLBOARD = "zhihu_billboard";
    public static final String INDEX_COVID19 = "covid19_info";
    public static final String INDEX_HOT_KEYWORD = "hot_keyword";

    /**
     * 获取索引常量集合
     * @return Set<String> 索引常量集合
     * @throws IllegalAccessException
     */
    public static Set<String> getIndicesSet() throws IllegalAccessException {
        Class<IndicesConstant> clazz = IndicesConstant.class;
        Field[] fields = clazz.getFields();
        Set<String> set = new HashSet<>(fields.length);
        for (Field field : fields) {
            set.add(field.get(clazz).toString());
        }
        return set;
    }

    /**
     * 获取索引常量映射
     * @return Map<String, String> 索引常量映射
     * @throws IllegalAccessException
     */
    public static Map<String, String> getIndicesMap() throws IllegalAccessException {
        Class<IndicesConstant> clazz = IndicesConstant.class;
        Field[] fields = clazz.getFields();

        Map<String, String> map = new HashMap<>();
        for (Field field : fields) {
            map.put(field.getName(), field.get(clazz).toString());
        }

        return map;
    }
}
