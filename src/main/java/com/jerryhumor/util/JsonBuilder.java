package com.jerryhumor.util;

import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

public class JsonBuilder {

    /**
     * 生成新闻列表json数据
     * @param newsPage
     * @return
     */
    public static String generateNewsListJson(Page<News> newsPage){
        String json = null;
        if (newsPage != null){
            //生成头部数据
            json = "{" +
                    "\"status\":\"ok\"," +
                    "\"total_row\":" + newsPage.getTotalRow() + "," +
                    "\"page_num\":" + newsPage.getPageNumber() + "," +
                    "\"page_size\":" + newsPage.getPageSize() + "," +
                    "\"is_first_page\":" + newsPage.isFirstPage() + "," +
                    "\"is_false_page\":" + newsPage.isLastPage() + "," +
                    "\"total_page\":" + newsPage.getTotalPage() + ",";

            //生成新闻数据
            json += "\"data\":" +
                    "[";
            List<News> newsList = newsPage.getList();
            if (newsList.size() > 0){
                //有数据的时候
                for (News news : newsList){
                    json += generateNewsJson(news);
                    json += ",";
                }
                //去除最后一个逗号
                json = json.substring(0, json.length() - 1);
                json += "]}";
            }else{
                //没有数据的时候
                json += "]}";
            }
        }
        return json;
    }

    /**
     * 生成新闻json数据
     * @param news
     * @return
     */
    public static String generateNewsJson(News news){
        //todo 生成新闻json数据
        String json = null;
        if (news != null){
            json = "{" +
                   "\"title\":\"" + news.getTitle() + "\"," +
                   "\"type\":" + news.getCategory() + "," +
                   "\"source\":" + news.getSource() + "," +
                   "\"img\":\"" + news.getImg() + "\"," +
                   "\"news_time\":" + news.getNewsTime().getTime() / 1000 + "," +
                   "\"create_time\":" + news.getCreateTime().getTime() / 1000 + "," +
                   "\"news_id\":\"" + news.getNewsId() + "\"" +
                   "}";
        }
        return json;
    }

    /**
     * 生成新闻文章json数据
     * @param article
     * @return
     */
    public static String generateArticleJson(Article article){
        //todo 生成新闻文章json数据
        String json = null;
        return json;
    }


}
