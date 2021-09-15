package com.lemon.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.lemon.utils.DatetimeUtil;
import com.lemon.utils.JsonUtil;
import com.lemon.utils.UUIDUtil;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * @author Liang Zhancheng
 * @date 2021/8/4 23:39
 */
@Data
@Getter
@Setter
@ToString
public class WeiboHot {
    @Field(type = FieldType.Keyword)
    private String id;
    private String rank;
    private String keyword;
    private String url;
    @Field(type = FieldType.Long)
    private Long count;
    private String flag;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second)
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date gmtCreate;

    public WeiboHot(String rank, String keyword, String url, String count, String flag) {
        this.id = UUIDUtil.getUUID();
        this.rank = rank;
        this.keyword = keyword;
        this.url = url;
        this.flag = flag;
        this.gmtCreate = new Date();
        if (count == null || "".equals(count.trim())) {
            this.count = null;
        } else {
            this.count = JsonUtil.isNumeric(count.trim()) ? Long.parseLong(count.trim()) : Long.parseLong(count.trim().substring(3));
        }
    }
}
