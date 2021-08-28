package com.lemon.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemon.pojo.WeiboHot;
import com.lemon.pojo.WeiboHotSearch;
import com.lemon.pojo.ZhihuBillboard;
import com.lemon.pojo.ZhihuTopSearch;
import com.lemon.utils.constant.UrlConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Liang Zhancheng
 * @date 2021/7/28 16:39
 */
public class HtmlParseUtil {
    public static void main(String[] args) throws IOException {
//        new HtmlParseUtil().parseWeiboHotSearch().forEach(System.out::println);
        new HtmlParseUtil().parseZhiuBillboard().forEach(System.out::println);
    }

    /**
     * 微博热搜榜
     *
     * @return
     * @throws IOException
     */
    public List<WeiboHotSearch> parseWeiboHotSearch() throws IOException {
        Document document = Jsoup.parse(new URL(UrlConstant.URL_WEIBO_HOT_SEARCH), 30000);
        Elements elements = document.getElementById("pl_top_realtimehot").getElementsByTag("tr");
        List<WeiboHotSearch> list = new ArrayList<>();

        for (int i = 1; i < elements.size(); i++) {
            String rank = elements.get(i).getElementsByClass("td-01 ranktop").eq(0).text().trim().length() == 0 ? "置顶" : elements.get(i).getElementsByClass("td-01 ranktop").eq(0).text();
            String keyword = elements.get(i).getElementsByClass("td-02").eq(0).text().split(" ")[0];
            String url = "https://s.weibo.com" + elements.get(i).getElementsByTag("a").attr("href");
            String count = elements.get(i).getElementsByTag("span").eq(0).text();
            String flag = elements.get(i).getElementsByClass("td-03").eq(0).text();

            WeiboHotSearch weiboHotSearch = new WeiboHotSearch(rank, keyword, url, count, flag);
            list.add(weiboHotSearch);
        }
        return list;
    }

    /**
     * 知乎热搜
     *
     * @return
     * @throws IOException
     */
    public List<ZhihuTopSearch> parseZhihuTopSearch() throws IOException {
        Document document = Jsoup.parse(new URL(UrlConstant.URL_ZHIHU_TOP_SEARCH), 30000);
        Element element = document.getElementById("js-initialData");
        JSONObject topSearch = JSONObject.parseObject(JSONObject.parseObject(JSONObject.parseObject(element.childNode(0).toString()).get("initialState").toString()).get("topsearch").toString());
        JSONArray data = JSONArray.parseArray(topSearch.get("data").toString());
        List<ZhihuTopSearch> list = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            JSONObject jsonObject = JSONObject.parseObject(data.get(i).toString());
            String title = jsonObject.get("queryDisplay").toString();
            String subTitle = jsonObject.get("queryDescription").toString().trim().length() <= 1 ? "" : jsonObject.get("queryDescription").toString();
            String url = "https://www.zhihu.com/search?q=" + URLEncoder.encode(jsonObject.get("realQuery").toString(), "UTF-8") + "&utm_content=search_hot&type=content";

            list.add(new ZhihuTopSearch(String.valueOf(i + 1), title, subTitle, url));
        }
        return list;
    }

    public List<ZhihuBillboard> parseZhiuBillboard() throws IOException {
        Document document = Jsoup.parse(new URL(UrlConstant.URL_ZHIHU_BILLBOARD), 30000);
        Element element = document.getElementById("js-initialData");
        JSONObject topStory = JSONObject.parseObject(JSONObject.parseObject(JSONObject.parseObject(element.childNode(0).toString()).get("initialState").toString()).get("topstory").toString());
        JSONArray hotList = JSONArray.parseArray(topStory.get("hotList").toString());
        List<ZhihuBillboard> list = new ArrayList<>();

        for (int i = 0; i < hotList.size(); i++) {
            JSONObject target = JSONObject.parseObject(JSONObject.parseObject(hotList.get(i).toString()).get("target").toString());
            String id = JSONObject.parseObject(hotList.get(i).toString()).get("id").toString();
            String title = JSONObject.parseObject(target.get("titleArea").toString()).get("text").toString();
            String excerpt = JSONObject.parseObject(target.get("excerptArea").toString()).get("text").toString();
            String cover = JSONObject.parseObject(target.get("imageArea").toString()).get("url").toString();
            String metrics = JSONObject.parseObject(target.get("metricsArea").toString()).get("text").toString();
            Integer answerCount = Integer.parseInt(JSONObject.parseObject(JSONObject.parseObject(hotList.get(i).toString()).get("feedSpecific").toString()).get("answerCount").toString());
            String url = JSONObject.parseObject(target.get("link").toString()).get("url").toString();

            list.add(new ZhihuBillboard(id, String.valueOf(i + 1), title, excerpt, cover, metrics, answerCount, url));
        }

        return list;
    }

    public List<WeiboHot> parseWeiboHot() throws IOException {
        Document document = Jsoup.parse(new URL(UrlConstant.URL_WEIBO_HOT_SEARCH), 30000);
        Elements elements = document.getElementById("pl_top_realtimehot").getElementsByTag("tr");
        List<WeiboHot> list = new ArrayList<>();

        for (int i = 1; i < elements.size(); i++) {
            String rank = elements.get(i).getElementsByClass("td-01 ranktop").eq(0).text().trim().length() == 0 ? "置顶" : elements.get(i).getElementsByClass("td-01 ranktop").eq(0).text();
            String keyword = elements.get(i).getElementsByClass("td-02").eq(0).text().split(" ")[0];
            String url = "https://s.weibo.com" + elements.get(i).getElementsByTag("a").attr("href");
            String count = elements.get(i).getElementsByTag("span").eq(0).text();
            String flag = elements.get(i).getElementsByClass("td-03").eq(0).text();

            WeiboHot weiboHot = new WeiboHot(rank, keyword, url, count, flag);
            list.add(weiboHot);
        }
        return list;
    }
}
