package com.lemon.service.impl;

import com.alibaba.fastjson.JSON;
import com.lemon.pojo.WeiboHot;
import com.lemon.pojo.WeiboHotSearch;
import com.lemon.pojo.ZhihuBillboard;
import com.lemon.pojo.ZhihuTopSearch;
import com.lemon.service.HotContentService;
import com.lemon.utils.HtmlParseUtil;
import com.lemon.utils.SpringUtil;
import com.lemon.utils.constant.IndicesConstant;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Liang Zhancheng
 * @date 2021/8/1 9:14
 */
@Service
public class HotContentServiceImpl implements HotContentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotContentServiceImpl.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public Boolean parseContent(String service, String subService) throws IOException {
        // 把查询的数据放入 es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMinutes(1L));

        if ("weibo".equals(service)) {
            if ("hot-search".equals(subService)) {
                bulkRequest = parseWeiboHotSearch(bulkRequest, IndicesConstant.INDEX_WEIBO_HOT_SEARCH);
            } else if ("hot-keyword".equals(subService)) {
                bulkRequest = parseWeiboHot(bulkRequest, IndicesConstant.INDEX_HOT_KEYWORD, false);
            } else {
                return false;
            }
        } else if ("zhihu".equals(service)) {
            if ("top-search".equals(subService)) {
                bulkRequest = parseZhihuHot(bulkRequest, IndicesConstant.INDEX_ZHIHU_TOP_SEARCH);
            } else if ("billboard".equals(subService)) {
                bulkRequest = parseZhihuHot(bulkRequest, IndicesConstant.INDEX_ZHIHU_BILLBOARD);
            } else {
                return false;
            }
        } else {
            return false;
        }

        // 查询数据及时更新
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    @Override
    public Boolean parseContent(String service, String subService, Boolean flag) throws IOException {
        // 把查询的数据放入 es 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout(TimeValue.timeValueMinutes(1L));

        if ("weibo".equals(service)) {
            if ("hot-search".equals(subService)) {
                bulkRequest = parseWeiboHotSearch(bulkRequest, IndicesConstant.INDEX_WEIBO_HOT_SEARCH);
            } else if ("hot-keyword".equals(subService)) {
                bulkRequest = parseWeiboHot(bulkRequest, IndicesConstant.INDEX_HOT_KEYWORD, flag);
            } else {
                return false;
            }
        } else if ("zhihu".equals(service)) {
            if ("top-search".equals(subService)) {
                bulkRequest = parseZhihuHot(bulkRequest, IndicesConstant.INDEX_ZHIHU_TOP_SEARCH);
            } else if ("billboard".equals(subService)) {
                bulkRequest = parseZhihuHot(bulkRequest, IndicesConstant.INDEX_ZHIHU_BILLBOARD);
            } else {
                return false;
            }
        } else {
            return false;
        }

        // 查询数据及时更新
        bulkRequest.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);

        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return !bulk.hasFailures();
    }

    @Override
    public Long deleteContent(String index) {
        return SpringUtil.getBean(IndexServiceImpl.class).deleteContent(index);
    }

    @Override
    public Long deleteContentBeforeTime(String index) {
        return SpringUtil.getBean(IndexServiceImpl.class).deleteContent(index, new RangeQueryBuilder("gmtCreate").lt("now-8h").format("epoch_millis"));
    }

    @Override
    public List<Map<String, Object>> searchPage(String index, Integer pageNo, Integer pageSize) throws Exception {
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from((pageNo - 1) * pageSize);
        sourceBuilder.size(pageSize);

        MatchAllQueryBuilder builder = QueryBuilders.matchAllQuery();
        sourceBuilder.query(builder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }

        return list;
    }

    @Override
    public List<Map<String, Object>> searchKeyword(String index, String keyword, Integer pageNo, Integer pageSize) throws Exception {
        if (pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize < 1) {
            pageSize = 10;
        }
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        // 分页
        sourceBuilder.from((pageNo - 1) * pageSize);
        sourceBuilder.size(pageSize);

        MatchQueryBuilder builder = QueryBuilders.matchQuery("keyword", keyword);
        sourceBuilder.query(builder);
        sourceBuilder.sort("gmtCreate");
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            list.add(documentFields.getSourceAsMap());
        }

        return list;
    }

    private BulkRequest parseWeiboHotSearch(BulkRequest bulkRequest, String weiboIndex) throws IOException {
        deleteContent(weiboIndex);
        List<WeiboHotSearch> weiboHotSearches = new HtmlParseUtil().parseWeiboHotSearch();
        for (int i = 0; i < weiboHotSearches.size(); i++) {
            bulkRequest.add(new IndexRequest(weiboIndex)
                    .source(JSON.toJSONString(weiboHotSearches.get(i)), XContentType.JSON));
        }
        LOGGER.info("插入索引数据：{}", weiboIndex);
        return bulkRequest;
    }

    private BulkRequest parseWeiboHot(BulkRequest bulkRequest, String weiboIndex, Boolean delFlag) throws IOException {
        if (delFlag) {
            deleteContent(weiboIndex);
        }
        List<WeiboHot> weiboHotSearches = new HtmlParseUtil().parseWeiboHot();
        for (int i = 0; i < weiboHotSearches.size(); i++) {
            bulkRequest.add(new IndexRequest(weiboIndex)
                    .source(JSON.toJSONString(weiboHotSearches.get(i)), XContentType.JSON));
        }
        LOGGER.info("插入索引数据：{}", weiboIndex);
        return bulkRequest;
    }

    private BulkRequest parseZhihuHot(BulkRequest bulkRequest, String zhihuIndex) throws IOException {
        deleteContent(zhihuIndex);
        if (IndicesConstant.INDEX_ZHIHU_TOP_SEARCH.equals(zhihuIndex)) {
            List<ZhihuTopSearch> zhihuTopSearches = new HtmlParseUtil().parseZhihuTopSearch();
            for (ZhihuTopSearch zhihuTopSearch : zhihuTopSearches) {
                bulkRequest.add(new IndexRequest(zhihuIndex)
                        .source(JSON.toJSONString(zhihuTopSearch), XContentType.JSON));
            }
        } else if (IndicesConstant.INDEX_ZHIHU_BILLBOARD.equals(zhihuIndex)) {
            List<ZhihuBillboard> zhihuBillboards = new HtmlParseUtil().parseZhiuBillboard();
            for (ZhihuBillboard zhihuBillboard : zhihuBillboards) {
                bulkRequest.add(new IndexRequest(zhihuIndex)
                        .source(JSON.toJSONString(zhihuBillboard), XContentType.JSON));
            }
        }

        LOGGER.info("插入索引数据：{}", zhihuIndex);
        return bulkRequest;
    }
}
