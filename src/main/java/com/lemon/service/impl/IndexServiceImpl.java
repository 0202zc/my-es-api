package com.lemon.service.impl;

import com.lemon.service.IndexService;

import com.lemon.utils.elasticsearch.IndicesUtil;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeRequest;
import org.elasticsearch.action.admin.indices.forcemerge.ForceMergeResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.MatchAllQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Liang Zhancheng
 * @date 2021/8/4 10:06
 */
@Service
public class IndexServiceImpl implements IndexService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotContentServiceImpl.class);

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Override
    public String createIndex(String index) throws IOException {
        if (IndicesUtil.isExistedIndex(restHighLevelClient, index)) {
            return "索引已存在";
        }
        return IndicesUtil.createIndex(restHighLevelClient, 1, 0, index).toString();
    }

    @Override
    public String deleteIndex(String index) throws IOException {
        if (!IndicesUtil.isExistedIndex(restHighLevelClient, index)) {
            return "索引不存在";
        }
        return IndicesUtil.deleteIndex(restHighLevelClient, index).toString();
    }

    @Override
    public Long deleteContent(String index) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        // 更新时版本冲突
        request.setConflicts("proceed");
        // 设置查询条件，第一个参数是字段名，第二个参数是字段的值
        request.setQuery(new MatchAllQueryBuilder());
        // 批次大小
        request.setBatchSize(1000);
        // 并行
        request.setSlices(2);
        // 使用滚动参数来控制“搜索上下文”存活的时间
        request.setScroll(TimeValue.timeValueMinutes(10));
        // 超时
        request.setTimeout(TimeValue.timeValueMinutes(5));
        // 刷新索引
        request.setRefresh(true);
        try {
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            LOGGER.info("删除索引数据：{}", index);
            forceMerge(index, 1);
            return response.getStatus().getDeleted();
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public Long deleteContent(String index, DeleteByQueryRequest request) {
        request.indices(index);
        request.setConflicts("proceed");
        request.setBatchSize(1000).setSlices(2).setRefresh(true);
        request.setScroll(TimeValue.timeValueMinutes(10)).setTimeout(TimeValue.timeValueMinutes(2));
        try {
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            LOGGER.info("删除索引数据：{}", index);
            forceMerge(index, 1);
            return response.getStatus().getDeleted();
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public Long deleteContent(String index, QueryBuilder queryBuilder) {
        DeleteByQueryRequest request = new DeleteByQueryRequest(index);
        request.setQuery(queryBuilder);
        request.setConflicts("proceed");
        request.setBatchSize(1000).setSlices(2).setRefresh(true);
        request.setScroll(TimeValue.timeValueMinutes(10)).setTimeout(TimeValue.timeValueMinutes(2));
        try {
            BulkByScrollResponse response = restHighLevelClient.deleteByQuery(request, RequestOptions.DEFAULT);
            LOGGER.info("删除索引数据：{}", index);
            forceMerge(index, 1);
            return response.getStatus().getDeleted();
        } catch (IOException e) {
            e.printStackTrace();
            return -1L;
        }
    }

    @Override
    public Integer forceMerge(String index, Integer maxNumSegments) throws IOException {
        if (index == null || index.trim().length() == 0) {
            return 0;
        }
        if (maxNumSegments < 1) {
            maxNumSegments = 1;
        }
        ForceMergeRequest mergeRequest = new ForceMergeRequest(index);
        mergeRequest.maxNumSegments(maxNumSegments);
        ForceMergeResponse mergeResponse = restHighLevelClient.indices().forcemerge(mergeRequest, RequestOptions.DEFAULT);
        LOGGER.info("Force to merge index: {}", index);
        return mergeResponse.getSuccessfulShards();
    }
}
