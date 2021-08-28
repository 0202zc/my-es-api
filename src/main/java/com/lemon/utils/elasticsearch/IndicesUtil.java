package com.lemon.utils.elasticsearch;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * @author Liang Zhancheng
 * @date 2021/8/6 10:32
 * @description 索引工具类
 */
public class IndicesUtil {

    private static RestHighLevelClient createRestHighLevelClient() {
        return new RestHighLevelClient(
                RestClient.builder(new HttpHost("127.0.0.1", 9200, "http"))
        );
    }

    /**
     * 创建单个索引
     *
     * @param restHighLevelClient ES 高级客户端
     * @param shards              索引分片数
     * @param replicas            索引副本数
     * @param index               索引名称
     * @return true：创建成功；false：创建失败
     * @throws IOException 异常
     */
    public static Boolean createIndex(RestHighLevelClient restHighLevelClient, Integer shards, Integer replicas, String index) throws IOException {
        boolean flag = false;
        if (restHighLevelClient == null) {
            restHighLevelClient = createRestHighLevelClient();
            flag = true;
        }

        CreateIndexRequest request = new CreateIndexRequest(index);
        request.settings(Settings.builder()
                .put("index.number_of_shards", shards == null || shards < 1 ? 1 : shards)
                .put("index.number_of_replicas", replicas == null || replicas < 0 ? 0 : replicas)
        );
        request.setTimeout(new TimeValue(60, TimeUnit.SECONDS));

        CreateIndexResponse createIndexResponse = restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        Boolean acknowledged = createIndexResponse.isAcknowledged();

        if (flag) {
            restHighLevelClient.close();
        }
        return acknowledged;
    }

    /**
     * 删除索引
     * @param restHighLevelClient ES 高级客户端
     * @param index 索引名称
     * @return true：创建成功；false：创建失败
     * @throws IOException
     */
    public static Boolean deleteIndex(RestHighLevelClient restHighLevelClient, String index) throws IOException {
        boolean flag = false;
        if (restHighLevelClient == null) {
            restHighLevelClient = createRestHighLevelClient();
            flag = true;
        }
        DeleteIndexRequest request = new DeleteIndexRequest(index);
        AcknowledgedResponse response = restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
        boolean acknowledged = response.isAcknowledged();

        if (flag) {
            restHighLevelClient.close();
        }
        return acknowledged;
    }

    /**
     * 获取指定索引
     * @param restHighLevelClient
     * @param index
     * @return GetIndexResponse
     * @throws IOException
     */
    public static GetIndexResponse getIndex(RestHighLevelClient restHighLevelClient, String index) throws IOException {
        boolean flag = false;
        if (restHighLevelClient == null) {
            restHighLevelClient = createRestHighLevelClient();
            flag = true;
        }

        GetIndexRequest request = new GetIndexRequest(index);
        GetIndexResponse getIndexResponse = restHighLevelClient.indices().get(request, RequestOptions.DEFAULT);

        if (flag) {
            restHighLevelClient.close();
        }
        return getIndexResponse;
    }

    /**
     * 判断索引是否存在
     * @param restHighLevelClient 高级客户端
     * @param index 索引
     * @return true: 存在; false: 不存在
     * @throws IOException
     */
    public static Boolean isExistedIndex(RestHighLevelClient restHighLevelClient, String index) throws IOException {
        boolean flag = false;
        if (restHighLevelClient == null) {
            restHighLevelClient = createRestHighLevelClient();
            flag = true;
        }

        GetIndexRequest request = new GetIndexRequest(index);
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);

        if (flag) {
            restHighLevelClient.close();
        }
        return exists;
    }

}
