package com.lemon.controller;

import com.alibaba.fastjson.JSON;
import com.lemon.service.HotContentService;
import com.lemon.utils.constant.IndicesConstant;
import com.lemon.utils.JsonUtil;
import com.lemon.utils.constant.ServicesConstant;
import com.lemon.utils.constant.SubServicesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

/**
 * @author Liang Zhancheng
 * @date 2021/8/1 10:09
 */
@RestController
@RequestMapping("/hot")
public class HotContentController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotContentController.class);

    @Autowired
    private HotContentService hotContentService;

    /**
     * 仅获取数据
     *
     * @param response
     * @param service  服务名称
     * @return
     */
    @GetMapping("/v1/parse/{service}/{sub-service}")
    public String parse(HttpServletResponse response, @PathVariable("service") String service, @PathVariable("sub-service") String subService, @RequestParam("clear") Boolean clear, @RequestParam("count") Integer count) {
        try {
            if (count == null || count < 1) {
                // 设置默认爬取30次
                count = 30;
            }
            LOGGER.info("刷新数据：{} - {}", service, subService);
            LOGGER.info("add {}", new Date());
            Boolean res = hotContentService.parseContent(service, subService, clear);
            for (int i = 1; i < count; i++) {
                Thread.sleep(60000);
                LOGGER.info("add {}", new Date());
                res = hotContentService.parseContent(service, subService);
            }
            return JsonUtil.toJsonString(response.getStatus(), JsonUtil.SUCCESS_STRING, res.toString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, JsonUtil.FAIL_STRING, e.toString());
        }
    }

    /**
     * 仅查询数据
     *
     * @param response
     * @param service  服务名称
     * @param pageNo   页号
     * @param pageSize 页大小
     * @return
     */
    @GetMapping("/v1/search/{service}/{sub-service}")
    public String search(HttpServletResponse response, @PathVariable("service") String service, @PathVariable("sub-service") String subService, @RequestParam("page") Integer pageNo, @RequestParam("per_page") Integer pageSize) {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        try {
            if ("weibo".equals(service)) {
                if ("hot-search".equals(subService)) {
                    service = IndicesConstant.INDEX_WEIBO_HOT_SEARCH;
                } else if ("hot-keyword".equals(subService)) {
                    service = IndicesConstant.INDEX_HOT_KEYWORD;
                }
            } else if ("zhihu".equals(service)) {
                if ("top-search".equals(subService)) {
                    service = IndicesConstant.INDEX_ZHIHU_TOP_SEARCH;
                } else if ("billboard".equals(subService)) {
                    service = IndicesConstant.INDEX_ZHIHU_BILLBOARD;
                }
            } else {
                return JsonUtil.toJsonString(404, "没有该项服务，目前可用的服务名有（weibo，zhihu）");
            }
            return JSON.toJSONString(hotContentService.searchPage(service, pageNo, pageSize));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, e.toString());
        }
    }

    /**
     * 刷新后查询数据
     *
     * @param response
     * @param service  服务名称
     * @param pageNo   页号
     * @param pageSize 页大小
     * @return JSON -> {data, status_code, message}
     * beta 版本
     */
    @GetMapping("/v2/{service}/{sub-service}")
    public String searchByRefresh(HttpServletResponse response, @PathVariable("service") String service, @PathVariable("sub-service") String subService, @RequestParam("page") Integer pageNo, @RequestParam("per_page") Integer pageSize, Boolean flag) {
        LOGGER.info("刷新并获取数据：{}", service);
        try {
            if (!ServicesConstant.getServicesSet().contains(service)) {
                return JsonUtil.toJsonString(404, "没有该项服务，目前可用的服务名有：" + ServicesConstant.getServicesSet());
            } else if (!SubServicesConstant.getSubServicesSet().contains(service + "/" + subService)) {
                return JsonUtil.toJsonString(404, "没有该项服务，目前可用的子服务名有：" + SubServicesConstant.getSubServicesSet());
            }
            if (hotContentService.parseContent(service, subService, flag)) {
                return search(response, service, subService, pageNo, pageSize);
            } else {
                return JsonUtil.toJsonString(500, "数据获取失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, e.toString());
        }
    }

    @GetMapping("/v2/weibo/hot-keyword/{keyword}2")
    public String searchKeyword(@PathVariable("keyword") String keyword, @RequestParam("page") Integer pageNo, @RequestParam("per_page") Integer pageSize) {
        LOGGER.info("查询关键词：{}", keyword);
        try {
            return JSON.toJSONString(hotContentService.searchKeyword("hot_keyword", keyword, pageNo, pageSize));
        } catch (Exception e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, e.toString());
        }
    }

}
