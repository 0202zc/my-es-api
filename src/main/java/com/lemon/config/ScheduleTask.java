package com.lemon.config;

import com.lemon.service.impl.HotContentServiceImpl;
import com.lemon.utils.SpringUtil;
import com.lemon.utils.constant.ServicesConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * @author Liang Zhancheng
 * @date 2021/8/7 13:48
 * 定时任务
 */
@Configuration
@EnableScheduling
public class ScheduleTask {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleTask.class);

    /**
     * 一分钟执行一次
     * <p>
     * Cron表达式参数分别表示：
     * 秒（0~59） 例如0/5表示每5秒
     * 分（0~59）
     * 时（0~23）
     * 日（0~31）的某天，需计算
     * 月（0~11）
     * 周几（ 可填1-7 或 SUN/MON/TUE/WED/THU/FRI/SAT）
     */
//    每隔5秒执行一次：*/5 * * * * ?
//    每隔1分钟执行一次：0 */1 * * * ?
//    每天23点执行一次：0 0 23 * * ?
//    每天凌晨1点执行一次：0 0 1 * * ?
//    每月1号凌晨1点执行一次：0 0 1 1 * ?
//    每月最后一天23点执行一次：0 0 23 L * ?
//    每周星期天凌晨1点实行一次：0 0 1 ? * L
//    在26分、29分、33分执行一次：0 26,29,33 * * * ?
//    每天的0点、13点、18点、21点都执行一次：0 0 0,13,18,21 * * ?
    @Scheduled(cron = "0 */1 0,6-23 * * ?")
//    @Scheduled(fixedRate = 60000)
    private void configureTasks() throws IOException {
        LOGGER.info("执行定时任务时间: {}", LocalDateTime.now());
        parse();
        lineChart();
    }

    private void parse() throws IOException {
        LOGGER.info("解析网页数据");
        SpringUtil.getBean(HotContentServiceImpl.class).parseContent(ServicesConstant.SERVICE_WEIBO, "hot-search");
        SpringUtil.getBean(HotContentServiceImpl.class).parseContent(ServicesConstant.SERVICE_ZHIHU, "billboard");
        SpringUtil.getBean(HotContentServiceImpl.class).parseContent(ServicesConstant.SERVICE_ZHIHU, "top-search");
    }

    private void lineChart() {
        LOGGER.info("折线图数据更新");
        try {
            SpringUtil.getBean(HotContentServiceImpl.class).parseContent("weibo", "hot-keyword");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(fixedRate = 300000)
    private void deleteContent() {
        LOGGER.info("清理{}过期数据", "hot_keyword");
        SpringUtil.getBean(HotContentServiceImpl.class).deleteContentBeforeTime("hot_keyword");
    }
}
