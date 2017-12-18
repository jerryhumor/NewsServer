package com.jerryhumor.news.controller;

import com.jerryhumor.news.constant.NewsType;
import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.util.JsonBuilder;
import com.jerryhumor.util.TimeUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.util.HashMap;

public class NewsController extends Controller {

    public void index(){
        renderText("hahah");
    }

    /**
     * 返回新闻列表
     * url携带四个参数 pageNum, pageSize, newsType, firstNewsCreateTime
     * pageNum                      页数
     * pageSize                     一页数量
     * newsType                     新闻类型
     * firstNewsCreateTime          第一次请求的第一条新闻的创建时间 long类型  单位秒
     */
    public void news(){
        boolean isParaCorrect = true;
        String json = null;
        int pageNum = getParaToInt(0);
        int pageSize = getParaToInt(1);
        int newsType = getParaToInt(2);
        long firstNewsCreateTime = getParaToLong(3);
        if (newsType >= NewsType.CODE_UNKNOWN || newsType < 0){
            isParaCorrect = false;
            json = generateErrorJson("新闻类型不存在");
        }
        if (isParaCorrect){
            String select = "select *";
            String sqlExceptSelect =
                    "from news " +
                    "where category = " + newsType +
                    " and unix_timestamp(create_time) >= unix_timestamp('" + TimeUtil.getTimeFormatted(firstNewsCreateTime, TimeUtil.FORMAT_DEFAULT) +
                    "')";
            Page<News> newsPage = News.dao.paginate(pageNum, pageSize, select, sqlExceptSelect);
            json = JsonBuilder.generateNewsListJson(newsPage);
        }
        renderJson(json);
    }

    /**
     * 返回特定文章内容
     * url携带一个参数 新闻所对应文章的id
     */
    public void article(){
        int articleId = getParaToInt();
        Article article = Article.dao.findById(articleId);
        renderText(JsonBuilder.generateArticleJson(article));
    }

    private String generateErrorJson(String error){
        return "{\"status\":\"failed\", \"error\":\"" + error + "\"}";
    }



}
