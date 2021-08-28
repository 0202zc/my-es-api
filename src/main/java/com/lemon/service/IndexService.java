package com.lemon.service;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;

import java.io.IOException;

/**
 * @author Liang Zhancheng
 * @date 2021/8/4 10:05
 * @description
 */
public interface IndexService {
    String createIndex(String index) throws IOException;

    String deleteIndex(String index) throws IOException;

    /**
     * 删除索引中所有内容
     * @param index 索引名称
     * @return
     */
    Long deleteContent(String index);

    /**
     * 自定义删除索引内容
     * @param index 索引名称
     * @param request DeleteByQueryRequest
     * @return
     */
    Long deleteContent(String index, DeleteByQueryRequest request);

    /**
     * 自定义查询条件删除索引内容
     * @param index 索引名称
     * @param queryBuilder 查询条件
     * @return
     */
    Long deleteContent(String index, QueryBuilder queryBuilder);

    /**
     * 强制合并段
     * @param index 索引
     * @param maxNumSegments 合并后的最大段个数
     * @throws IOException
     */
    Integer forceMerge(String index, Integer maxNumSegments) throws IOException;
}
