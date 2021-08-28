package com.lemon.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lemon.utils.DatetimeUtil;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

/**
 * @author Liang Zhancheng
 * @date 2021/8/3 16:54
 * 知乎热榜
 */
@Data
@ToString
@Getter
@Setter
public class ZhihuBillboard implements Serializable {
    @Field(type = FieldType.Keyword)
    private String id;
    private String rank;
    private String title;
    private String excerpt; // 摘录
    private String cover;   // 封面
    private String metrics;    // 热度
    private Integer answerCount;
    private String url;

    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss:SSS", timezone = "GMT+8")
    private String gmtCreate;

    public ZhihuBillboard(String id, String rank, String title, String excerpt, String cover, String metrics, Integer answerCount, String url) {
        this.id = id;
        this.rank = rank;
        this.title = title;
        this.excerpt = excerpt.length() == 0 ? " " : excerpt;
        this.cover = cover.replaceAll("\\u002F", "/");
        this.metrics = metrics.replaceAll(" 万热度", "").trim();
        this.answerCount = answerCount;
        this.url = url.replaceAll("\\u002F", "/");
        this.gmtCreate = DatetimeUtil.getFormatDatetime();
    }
}
