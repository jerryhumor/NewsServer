package com.jerryhumor.spider;

import com.google.gson.Gson;
import com.jerryhumor.classifier.ArticleClassifier;
import com.jerryhumor.news.constant.NewsSource;
import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.spider.bean.ZhihuArticle;
import com.jerryhumor.spider.bean.ZhihuListResponse;
import com.jerryhumor.spider.bean.ZhihuStory;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class ZhihuCollector {

    private static final String URL_LATEST_NEWS = "https://news-at.zhihu.com/api/4/news/latest";

    private static final String URL_PREFIX = "https://news-at.zhihu.com/api/4/news/";
    private static HashSet<String> mNewsIdSet = new HashSet<String>();

    private ArticleClassifier mClassifier;
    private Gson mGson;
    private OkHttpClient mClient;

    public void startGather(){
        System.out.println("知乎收集器开始工作");
        init();
        List<ZhihuStory> stories = getNewsList();
        List<ZhihuStory> filteredStories = getFilteredNewsIdList(stories);
        List<ZhihuArticle> articles = new ArrayList<ZhihuArticle>();
        for (ZhihuStory story : filteredStories){
            ZhihuArticle article = getArticle(URL_PREFIX + story.getNewsId());
            final String title = article.getTitle();
            final int category = getCategory(article.getBody());
            final Date date = new Date();
            final String body = article.getBody().replace("\"", "\\\"");
            saveNews(title, NewsSource.CODE_ZHIHU, category, null, date, story.getNewsId() + "", body);
        }
        System.out.println("知乎收集器工作结束");
    }

    /**
     * 获取url中的newsId 查重 返回需要爬取的新闻
     * @param stories
     * @return
     */
    private List<ZhihuStory> getFilteredNewsIdList(List<ZhihuStory> stories){
        List<ZhihuStory> filteredStories = new ArrayList<ZhihuStory>();
        for (ZhihuStory story : stories){
            if (isStoryValid(story)){
                filteredStories.add(story);
            }else{
                System.out.println("get filter news id, already had this news");
            }
        }
        System.out.println("过滤过的新闻列表数量为" + filteredStories.size());
        return filteredStories;
    }

    private boolean isStoryValid(ZhihuStory story){
        return mNewsIdSet.add(story.getNewsId() + "");
    }

    private int getCategory(String body) {
        Element element = Jsoup.parse(body);
        return mClassifier.classify(element.text());
    }

    private void init(){
        mClassifier = ArticleClassifier.getInstance();
        mGson = new Gson();
        mClient = new OkHttpClient();
    }

    public static void main(String[] args){
        ZhihuCollector collector = new ZhihuCollector();
        collector.startGather();
    }

    public static void testUrl(String url){
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()){
                System.out.println(response.body().string());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private List<ZhihuStory> getNewsList(){
        List<ZhihuStory> stories = null;
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(URL_LATEST_NEWS).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()){
                ZhihuListResponse zhihuListResponse = mGson.fromJson(response.body().string(), ZhihuListResponse.class);
                stories = zhihuListResponse.getStories();
            }else{
                System.out.println("访问失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return stories;
    }

    /**
     * 保存新闻到数据库
     * @param title
     * @param source
     * @param category
     * @param img
     * @param newsTime
     * @param newsId
     * @param content
     */
    public void saveNews(final String title, final int source, final int category, final String img,
                         final Date newsTime, final String newsId, final String content){
        Article article = new Article()
                .setContent(content);
        article.save();
        News news = new News()
                .setTitle(title)
                .setSource(source)
                .setCategory(category)
                .setImg(img)
                .setNewsTime(newsTime)
                .setCreateTime(new Date())
                .setNewsId("zhihu" + newsId)
                .setFkArticleId(article.getId());
        news.save();
    }

    private ZhihuArticle getArticle(String url){
        System.out.println("get article, url: " + url);
        ZhihuArticle article = null;
        try{
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            Call call = client.newCall(request);
            Response response = call.execute();
            if (response.isSuccessful()){
                article = mGson.fromJson(response.body().string(), ZhihuArticle.class);
            }else{
                System.out.println("访问失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return article;
    }


}
