package com.lemon.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lemon.utils.DatetimeUtil;
import com.lemon.utils.UUIDUtil;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * @author Liang Zhancheng
 * @date 2021/7/31 18:06
 * 知乎热搜
 */
@Data
@ToString
@Getter
@Setter
public class ZhihuTopSearch {
    @Field(type = FieldType.Keyword)
    private String id;
    private String rank;
    private String keyword;
    private String subTitle;
    private String url;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS", timezone = "GMT+8")
    private String gmtCreate;

    public ZhihuTopSearch(String rank, String title, String subTitle, String url) {
        this.id = UUIDUtil.getUUID();
        this.rank = rank;
        this.keyword = title;
        this.subTitle = subTitle;
        this.url = url;
        this.gmtCreate = DatetimeUtil.getFormatDatetime();
    }
}
