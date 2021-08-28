package com.lemon.utils;

import java.util.UUID;

/**
 * @author Liang Zhancheng
 * @date 2021/7/31 23:48
 */
public class UUIDUtil {
    /**
     * 生成UUID
     *
     * @return UUID
     */
    public static String getUUID() {
        UUID uuid = UUID.randomUUID();
        String str = uuid.toString();
        String uuidStr = str.replace("-", "");
        return uuidStr;
    }
}
