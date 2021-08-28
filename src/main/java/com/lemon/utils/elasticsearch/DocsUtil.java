package com.lemon.utils.elasticsearch;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Liang Zhancheng
 * @date 2021/8/6 11:06
 * @description 文档工具类
 */
public class DocsUtil {

    private static final Logger logger = LoggerFactory.getLogger(DocsUtil.class);

    /**
     * 自定义查询条件，需要自己new SourceBuilder()
     * @param restHighLevelClient 高级客户端
     * @param sourceBuilder 查询条件
     * @param index 索引
     * @return
     * @throws IOException
     */
    public static List<Map<String, Object>> get(RestHighLevelClient restHighLevelClient, SearchSourceBuilder sourceBuilder, String index) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(index).source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        logger.info("共查询到{}条数据，耗时：{}", hits.getTotalHits(), response.getTook());

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            mapList.add(documentFields.getSourceAsMap());
        }

        return mapList;
    }

    /**
     * 插入数据到文档
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引名称
     * @param id                  数据id
     * @param data                数据内容（Pojo类）
     * @return result
     * @throws IOException
     */
    public static DocWriteResponse.Result insertDoc(RestHighLevelClient restHighLevelClient, String index, String id, Object data) throws IOException {
        // 插入数据
        IndexRequest request = new IndexRequest();
        request.index(index).id(id);

        // 向ES插入数据，必须转成JSON格式
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(data);
        request.source(json, XContentType.JSON);

        IndexResponse response = restHighLevelClient.index(request, RequestOptions.DEFAULT);

        return response.getResult();
    }

    /**
     * 删除指定id的数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param id                  数据id
     * @return
     * @throws IOException
     */
    public static DocWriteResponse.Result deleteDoc(RestHighLevelClient restHighLevelClient, String index, String id) throws IOException {
        DeleteRequest request = new DeleteRequest();
        request.index(index).id(id);
        DeleteResponse response = restHighLevelClient.delete(request, RequestOptions.DEFAULT);

        return response.getResult();
    }

    /**
     * 获取指定id的数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param id                  数据id
     * @return 指定id的数据字符串
     * @throws IOException
     */
    public static String getDoc(RestHighLevelClient restHighLevelClient, String index, String id) throws IOException {
        GetRequest request = new GetRequest();
        request.index(index).id(id);
        GetResponse response = restHighLevelClient.get(request, RequestOptions.DEFAULT);

        return response.getSourceAsString();
    }

    /**
     * 局部修改指定id的数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param id                  数据id
     * @param key                 数据字段
     * @param value               数据字段值
     * @return
     * @throws IOException
     */
    public static DocWriteResponse.Result updateDoc(RestHighLevelClient restHighLevelClient, String index, String id, String key, Object value) throws IOException {
        UpdateRequest request = new UpdateRequest();
        request.index(index).id(id);
        // 局部修改
        request.doc(XContentType.JSON, key, value);

        UpdateResponse response = restHighLevelClient.update(request, RequestOptions.DEFAULT);

        return response.getResult();
    }

    /**
     * 批量插入数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param data                数据列表
     * @return true: 插入成功；false: 插入失败
     * @throws IOException
     */
    public static Boolean insertDocBulk(RestHighLevelClient restHighLevelClient, String index, List<Object> data) throws IOException {
        BulkRequest request = new BulkRequest();
        for (Object item : data) {
            request.add(new IndexRequest(index).source(JSON.toJSONString(item), XContentType.JSON));
        }
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        return !restHighLevelClient.bulk(request, RequestOptions.DEFAULT).hasFailures();
    }

    /**
     * 批量删除数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param ids                 删除的数据id
     * @return true: 删除成功；false: 删除失败
     * @throws IOException
     */
    public static Boolean deleteDocBulk(RestHighLevelClient restHighLevelClient, String index, String[] ids) throws IOException {
        BulkRequest request = new BulkRequest();
        for (String id : ids) {
            request.add(new DeleteRequest().index(index).id(id));
        }
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        return !restHighLevelClient.bulk(request, RequestOptions.DEFAULT).hasFailures();
    }

    /**
     * 查询全部数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @return 查询结果 Map列表
     * @throws IOException
     */
    public static List<Map<String, Object>> matchAllQuery(RestHighLevelClient restHighLevelClient, String index) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(index);
        // 查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        request.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 查询结果 匹配的数据
        SearchHits hits = response.getHits();
        logger.info("共查询到{}条数据，耗时：{}", hits.getTotalHits(), response.getTook());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            list.add(documentFields.getSourceAsMap());
        }

        return list;
    }

    /**
     * 分页查询全部数据
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param pageNo              页号
     * @param pageSize            页大小
     * @return 查询结果 Map列表
     * @throws IOException
     */
    public static List<Map<String, Object>> matchAllQuery(RestHighLevelClient restHighLevelClient, String index, Integer pageNo, Integer pageSize) throws IOException {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 1 ? 10 : pageSize;

        SearchRequest request = new SearchRequest();
        request.indices(index);
        // 查询条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchAllQuery());
        sourceBuilder.from((pageNo - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 查询结果 匹配的数据
        SearchHits hits = response.getHits();
        logger.info("共查询到{}条数据，耗时：{}", hits.getTotalHits(), response.getTook());

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            list.add(documentFields.getSourceAsMap());
        }

        return list;
    }

    /**
     * 条件查询
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param field               待查询的字段名
     * @param value               待查询的值
     * @return 查询结果 Map列表
     * @throws IOException
     */
    public static List<Map<String, Object>> termQuery(RestHighLevelClient restHighLevelClient, String index, String field, String value) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery(field, value));
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 查询结果 匹配的数据
        SearchHits hits = response.getHits();
        logger.info("共查询到{}条数据，耗时：{}", hits.getTotalHits(), response.getTook());

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            mapList.add(documentFields.getSourceAsMap());
        }

        return mapList;
    }

    /**
     * 分页条件查询
     *
     * @param restHighLevelClient 高级客户端
     * @param index               索引
     * @param field               待查询的字段名
     * @param value               待查询的值
     * @param pageNo              页号
     * @param pageSize            页大小
     * @return 查询结果 Map列表
     * @throws IOException
     */
    public static List<Map<String, Object>> termQuery(RestHighLevelClient restHighLevelClient, String index, String field, String value, Integer pageNo, Integer pageSize) throws IOException {
        pageNo = pageNo < 1 ? 1 : pageNo;
        pageSize = pageSize < 1 ? 10 : pageSize;

        SearchRequest request = new SearchRequest();
        request.indices(index);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.termQuery(field, value));
        sourceBuilder.from((pageNo - 1) * pageSize);
        sourceBuilder.size(pageSize);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        request.source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);

        // 查询结果 匹配的数据
        SearchHits hits = response.getHits();
        logger.info("共查询到{}条数据，耗时：{}", hits.getTotalHits(), response.getTook());

        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            mapList.add(documentFields.getSourceAsMap());
        }

        return mapList;
    }

    /**
     * 高亮查询
     * @param restHighLevelClient 高级客户端
     * @param sourceBuilder 查询条件
     * @param index 索引
     * @param fieldsToHighlight 需要高亮的字段列表
     * @return 高亮查询的结果列表
     * @throws IOException
     */
    public static List<Map<String, Object>> getWithHighlight(RestHighLevelClient restHighLevelClient, SearchSourceBuilder sourceBuilder, String index, List<String> fieldsToHighlight) throws IOException {
        SearchRequest request = new SearchRequest();

        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<font color='red'>");
        highlightBuilder.postTags("</font>");

        for (String field : fieldsToHighlight) {
            highlightBuilder.field(field);
        }

        sourceBuilder.highlighter(highlightBuilder);
        request.indices(index).source(sourceBuilder);

        SearchResponse response = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits hits = response.getHits();
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (SearchHit documentFields : hits.getHits()) {
            mapList.add(documentFields.getSourceAsMap());
        }

        return mapList;
    }

}
