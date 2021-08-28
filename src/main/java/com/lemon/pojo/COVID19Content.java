package com.lemon.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Liang Zhancheng
 * @date 2021/7/31 23:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class COVID19Content implements Serializable {
    private String id;
    private String city;
}
