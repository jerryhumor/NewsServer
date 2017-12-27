package com.jerryhumor.spider;

import com.jerryhumor.classifier.ArticleClassifier;
import com.jerryhumor.news.constant.NewsSource;
import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.util.TimeUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.*;

public class SinaSpider {

    private static String URL_SINA_M = "http://m.sohu.com/ch/8/";

    private static String URL_PREFIX = "http://m.sohu.com/a/";

    private static String TAG_A_CLASS_PLAIN_TEXT = "plainText";
    private static String TAG_A_CLASS_ONE_PIC = "onePic";
    private static String TAG_A_CLASS_THREE_PIC = "threePic";

    private static HashSet<String> mNewsIdSet = new HashSet<String>();
    private static ArticleClassifier mClassifier;


//    public static void main(String[] args){
//        SinaSpider spider = new SinaSpider();
//        spider.startGather();
//    }

    public void startGather(){
        init();
        System.out.println("新浪爬虫开始工作");
        List<String> urlList = gatherNewsList();
        List<String> filteredNewsIdList = getFilteredNewsIdList(urlList);
        for (String str : filteredNewsIdList){
            gatherNews(str);
        }
        System.out.println("新浪爬虫工作结束");
    }

    private void init() {
        mClassifier = ArticleClassifier.getInstance();
    }

    public static void testUrl(String url){
        try{
            Document document = Jsoup.connect(url).get();
            System.out.println(document.toString());
        }catch (Exception e){
            e.printStackTrace();
        }
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
                .setNewsId("sina" + newsId)
                .setFkArticleId(article.getId());
        news.save();
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
     * 获取newsId
     * @param url
     * @return
     */
    private String getNewsId(final String url){
        System.out.println("get news id, url " + url);
        String newsId = null;
        String[] urlSplit = url.split("[?]");
        if (urlSplit.length != 2){
            System.out.println("get news id, url length is not 2");
        }else{
            String[] newsIdWithNoise = urlSplit[0].split("[/]");
            newsId = newsIdWithNoise[2];
        }
        return newsId;
    }

    /**
     * 获取首页新闻列表url
     * @return
     */
    public List<String> gatherNewsList() {
        List<String> urlList = new ArrayList<String>();
        try {
            Document newsDoc = Jsoup.connect(URL_SINA_M).get();
            Elements tagList = newsDoc.getElementsByTag("a");
            for (Element tag : tagList){
                if (TAG_A_CLASS_ONE_PIC.equals(tag.attr("class")) || TAG_A_CLASS_THREE_PIC.equals(tag.attr("class")) || TAG_A_CLASS_PLAIN_TEXT.equals(tag.attr("class"))){
                    urlList.add(tag.attr("href"));
                }else{
                    System.out.println("标签a不符合，class属性为：" + tag.attr("class"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return urlList;
    }

    private String generateNewsUrl(final String newsId){
        return URL_PREFIX + newsId;
    }

    /**
     * 采集新闻 根据url爬取新闻 分类整理 同时存入数据库
     * @param newsId
     */
    public void gatherNews(final String newsId){
        try{
            Document document = Jsoup.connect(generateNewsUrl(newsId)).get();
            Element articleElement = document.getElementById("articleContent");
            Element titleElement = getTitleElement(document);
            Element infoElement = getInfoElement(document);
            removeLookAllSection(articleElement);
            final String title = getArticleTitle(titleElement).replace("\"", "\\\\");
            final Date date = getArticleTime(infoElement);
            final String article = getArticle(document).replace("\"", "\\\"");
            final String image = getFirstImage(articleElement);
            final int category = getArticleCategory(articleElement);
            saveNews(title, NewsSource.CODE_SINA, category, image, date, newsId, article);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 从文章中获取文章信息元素
     * @param document
     * @return
     */
    private Element getInfoElement(Document document) {
        Elements infoElements = document.getElementsByClass("author-info");
        Element infoElement = null;
        if (infoElements != null && infoElements.size() > 0){
            infoElement = infoElements.get(0);
        }else{
            System.out.println("获取文章信息元素失败");
        }
        return infoElement;
    }

    /**
     * 获取文章的类型
     * @param articleElement
     * @return
     */
    private int getArticleCategory(Element articleElement) {
        return mClassifier.classify(articleElement.text());
    }

    /**
     * 从文章元素中获取第一张图片
     * @param articleElement
     * @return
     */
    private String getFirstImage(Element articleElement) {
        return null;
    }

    /**
     * 获取文章html
     * @param articleDocument
     * @return
     */
    private String getArticle(Document articleDocument) {
        StringBuilder articleStringBuilder = new StringBuilder();
        Element titleElement = articleDocument.getElementsByClass("title-info").get(0);
        Element infoElement = articleDocument.getElementsByClass("author-info").get(0);
        Element articleElement = articleDocument.getElementById("articleContent");
        articleStringBuilder.append(titleElement.toString());
        articleStringBuilder.append(infoElement.toString());
        articleStringBuilder.append(articleElement.toString());
        return articleStringBuilder.toString();
    }

    /**
     * 获取文章的标题信息
     * @param document
     * @return
     */
    private Element getTitleElement(Document document) {
        Elements infoElements = document.getElementsByClass("title-info");
        Element infoElement = null;
        if (infoElements != null && infoElements.size() > 0){
            infoElement =  infoElements.get(0);
        }else{
            System.out.println("获取标题信息元素失败");
        }
        return infoElement;
    }

    /**
     * 取出新闻中的查看更多按钮
     * @param articleElement
     */
    public void removeLookAllSection(Element articleElement){
        Elements lookAllElements = articleElement.getElementsByClass("look-all");
        if (lookAllElements != null && lookAllElements.size() > 0){
            lookAllElements.get(0).remove();
        }
    }

    /**
     * 从文章标题元素中获取文章标题
     * @param titleElement
     * @return
     */
    private String getArticleTitle(Element titleElement){
        return titleElement.text();
    }

    /**
     * 从文章标题元素中获取文章时间
     * @param infoElement
     * @return
     */
    private Date getArticleTime(Element infoElement){
        Elements timeElements = infoElement.getElementsByClass("time");
        Date articleTime = null;
        if (timeElements != null && timeElements.size() > 0){
            Element timeElement = timeElements.get(0);
            articleTime = TimeUtil.parseTime(timeElement.text(), "MM-dd HH:mm");
        }else{
            System.out.println("获取文章时间失败");
        }
        return articleTime;
    }



}
