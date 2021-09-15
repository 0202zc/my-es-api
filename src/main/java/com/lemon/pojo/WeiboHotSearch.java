package com.lemon.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lemon.utils.DatetimeUtil;
import com.lemon.utils.JsonUtil;
import com.lemon.utils.UUIDUtil;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author Liang Zhancheng
 * @date 2021/7/31 18:05
 * 微博热搜榜
 */
@Data
@Getter
@Setter
@ToString
public class WeiboHotSearch implements Serializable {
    @Field(type = FieldType.Keyword)
    private String id;
    private String rank;
    @Field(type = FieldType.Keyword)
    private String keyword;
    private String url;
    private Long count;
    private String flag;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS", timezone = "GMT+8")
    private String gmtCreate;

    public WeiboHotSearch(String rank, String keyword, String url, String count, String flag) {
        this.id = UUIDUtil.getUUID();
        this.rank = rank;
        this.keyword = keyword;
        this.url = url;
        this.flag = flag;
        this.gmtCreate = DatetimeUtil.getFormatDatetime();
        if (count == null || "".equals(count.trim())) {
            this.count = null;
        } else {
            this.count = JsonUtil.isNumeric(count.trim()) ? Long.parseLong(count.trim()) : Long.parseLong(count.trim().substring(3));
        }
    }
}
