package com.jerryhumor.spider;

import com.jerryhumor.classifier.ArticleClassifier;
import com.jerryhumor.news.constant.NewsSource;
import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.spider.constant.SpiderConstant;
import com.jerryhumor.util.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class NeteaseSpider {

    private static final String URL_PREFIX = "https://3g.163.com/all/article/";
    private static final String URL_ENT_PREFIX = "https://3g.163.com/ent/article/";

    private static final String URL_NETEASE_3G = "https://3g.163.com";

    private static HashSet<String> mNewsIdSet = new HashSet<String>();
    private static ArticleClassifier mClassifier;

    public static void start(){
        Timer spiderTimer = new Timer();
        spiderTimer.schedule(new NeteaseSpiderTask(), SpiderConstant.NETEAST_PERIOD, SpiderConstant.NETEAST_PERIOD);
    }

    public void startGather(){
        init();
        System.out.println("网易爬虫开始工作");
        List<String> urlList = gatherNewsList();
        List<String> filteredNewsIdList = getFilteredNewsIdList(urlList);
        for (String newsId : filteredNewsIdList){
            gatherNews(newsId);
        }
        System.out.println("网易爬虫结束工作");
    }

    /**
     * 初始化成员变量
     */
    public void init(){
        mClassifier = ArticleClassifier.getInstance();
    }

    /**
     * 采集新闻 根据url爬取新闻 分类整理 同时存入数据库
     * @param newsId
     */
    public void gatherNews(final String newsId){
        try{
            Document document = Jsoup.connect(generateNewsUrl(newsId)).get();
            final String articleId = generateArticleId(newsId);
            Element articleElement = document.getElementById(articleId);
            Elements headElements = articleElement.getElementsByClass("head");
            Elements contentElements = articleElement.getElementsByClass("content");
            final String title = getArticleTitle(headElements.get(0));
            final Date date = getArticleTime(headElements.get(0));
            final String article = generateArticleBody(headElements.get(0), contentElements.get(0));
            final String image = getFirstImage(contentElements.get(0));
            final int category = getArticleCategory(contentElements.get(0));
            saveNews(title, NewsSource.CODE_NETEASE, category, image, date, newsId, article);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取首页新闻列表url
     * @return
     */
    public List<String> gatherNewsList() {
        List<String> urlList = new ArrayList<String>();
        try {
            Document newsDoc = Jsoup.connect(URL_NETEASE_3G).get();
            Elements newsLists =  newsDoc.getElementsByClass("s_news_list");
            if (newsLists == null || newsLists.size() <= 0){
                System.out.println("gather news, newsLists error");
            }else{
                Element newsList = newsLists.get(0);
                Elements newsGroup = newsList.getElementsByTag("a");
                if (newsGroup != null && newsGroup.size() > 0){
                    for (Element news : newsGroup){
                        String url = getNewsUrl(news);
                        if (url != null){
                            urlList.add(url);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
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
                .setNewsId("netease" + newsId)
                .setFkArticleId(article.getId());
        news.save();
    }

    /**
     * 从Element中获取新闻url
     * @param liElement
     * @return
     */
    private String getNewsUrl(Element liElement){
        String url = null;
        Elements hrefElements = liElement.getElementsByTag("a");
        if (hrefElements == null || hrefElements.size() <= 0){
            System.out.println("get news url, href elements error");
        }else{
            url = hrefElements.get(0).attr("href");
            //todo 去掉最后的#等
        }
        return url;
    }

    /**
     * 根据newsId生成新闻url
     * @param newsId
     * @return
     */
    private String generateNewsUrl(String newsId){
        return URL_PREFIX + newsId + ".html";
    }

    /**
     * 获取newsId
     * @param url
     * @return
     */
    private String getNewsId(final String url){
        System.out.println("get news id, url " + url);
        String newsId = null;
        String[] urlSplit = url.split("/");
        if (urlSplit.length != 6){
            System.out.println("get news id, url length is not 6");
        }else{
            String urlWithNoise = urlSplit[5];
            String[] urlWithoutNoise = urlWithNoise.split("#");
            if (urlWithoutNoise == null || urlWithoutNoise.length != 2){
                System.out.println("url without noise error");
            }else{
                newsId = urlWithoutNoise[0].substring(0, urlWithoutNoise[0].length() - 5);
            }
        }
        return newsId;
    }

    /**
     * 生成文章id 用于在文章中查找文章主题
     * @param newsId
     * @return
     */
    private String generateArticleId(final String newsId){
        return "article-" + newsId;
    }

    /**
     * 从文章头部获取标题
     * @param headElement
     * @return
     */
    private String getArticleTitle(final Element headElement){
        Elements titleElements = headElement.getElementsByClass("title");
        Element titleElement = titleElements.get(0);
        return titleElement.ownText();
    }

    /**
     * 从文章头部信息获取时间
     * @param headElement
     * @return
     */
    private Date getArticleTime(final Element headElement){
        Elements timeElements = headElement.getElementsByClass("time");
        Element timeElement = timeElements.get(0);
        return TimeUtil.parseTime(timeElement.ownText(), TimeUtil.FORMAT_DEFAULT);
    }

    /**
     * 组合标题和新闻内容 同时生成客户端可以解析的数据
     * @param headElement
     * @param contentElement
     * @return
     */
    private String generateArticleBody(final Element headElement, final Element contentElement){
        //todo 做一些其他的操作 替换引号斜杠等等
        return headElement.toString() + contentElement.toString();
    }

    /**
     * 从内容中获取第一张图片 用户之后当做新闻图片展示
     * @param contentElement
     * @return
     */
    private String getFirstImage(final Element contentElement){
        return "测试图片";
    }

    /**
     * 用分类器分类 （估计要先去除标签等等）
     * @param contentElement
     * @return
     */
    private int getArticleCategory(final Element contentElement){
        return mClassifier.classify(contentElement.text());
    }

    /**
     * 获取url中的newsId 查重 返回需要爬取的新闻
     * @param urlList
     * @return
     */
    private List<String> getFilteredNewsIdList(List<String> urlList){
        List<String> filteredNewsIdList = new ArrayList<String>();
        for (String url : urlList){
            String newsId = getNewsId(url);
            if (isNewsIdValid(newsId)){
                filteredNewsIdList.add(newsId);
            }else{
                System.out.println("get filter news id, already had this news");
            }
        }
        System.out.println("过滤过的新闻列表数量为" + filteredNewsIdList.size());
        return filteredNewsIdList;
    }

    /**
     * 检测newsId是否有效 同时数据库查重
     * @param newsId
     * @return
     */
    private boolean isNewsIdValid(String newsId){
        boolean isValid = false;
        if (newsId != null && !"".equals(newsId)){
            isValid = mNewsIdSet.add(newsId);
        }
        return isValid;
    }

    public String getArticles(String articleId, String url){
        System.out.println("get article, article id is " + articleId);
        try{
            Document document = Jsoup.connect(url).get();
            Element articleElement = document.getElementById(generateArticleId(articleId));
            Elements contentElements = articleElement.getElementsByClass("content");
            String content = contentElements.get(0).text();
            return content;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
