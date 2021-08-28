package com.lemon.controller;

import com.lemon.service.IndexService;
import com.lemon.service.impl.HotContentServiceImpl;
import com.lemon.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Liang Zhancheng
 * @date 2021/8/3 8:45
 */
@RestController
@RequestMapping("/index")
public class IndexController {
    private static final Logger LOGGER = LoggerFactory.getLogger(HotContentServiceImpl.class);

    @Autowired
    private IndexService indexService;

    @PostMapping("/{index}")
    public String createIndex(HttpServletResponse response, @PathVariable("index") String index) throws IOException {
        try {
            return JsonUtil.toJsonString(response.getStatus(), indexService.createIndex(index));
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, e.toString());
        }
    }

    @DeleteMapping("/{index}")
    public String deleteIndex(HttpServletResponse response, @PathVariable("index") String index) {
        try {
            LOGGER.info("删除索引内容：{}", index);
            return JsonUtil.toJsonString(response.getStatus(), indexService.deleteIndex(index));
//            return JsonUtil.toJsonString(response.getStatus(), indexService.deleteContent(index) != -1L ? JsonUtil.SUCCESS_STRING : JsonUtil.FAIL_STRING);
        } catch (IOException e) {
            e.printStackTrace();
            return JsonUtil.toJsonString(500, e.toString());
        }
    }
}
