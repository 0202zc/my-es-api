package com.lemon.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Liang Zhancheng
 * @date 2021/8/1 13:22
 */
public class DatetimeUtil {
    public static String getFormatDatetime() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(date);
    }
}
