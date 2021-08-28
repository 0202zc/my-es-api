package com.lemon.service;

import org.elasticsearch.index.query.QueryBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author Liang Zhancheng
 * @date 2021/8/1 9:14
 */
public interface HotContentService {
    Boolean parseContent(String service, String subService) throws IOException;

    Boolean parseContent(String service, String subService, Boolean delFlag) throws IOException;

    Long deleteContent(String service);

    Long deleteContentBeforeTime(String service);

    List<Map<String, Object>> searchPage(String index, Integer pageNo, Integer pageSize) throws Exception;

    List<Map<String, Object>> searchKeyword(String index, String keyword, Integer pageNo, Integer pageSize) throws Exception;
}
