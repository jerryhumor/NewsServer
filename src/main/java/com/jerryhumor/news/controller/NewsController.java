package com.jerryhumor.news.controller;

import com.jerryhumor.news.constant.NewsType;
import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.push.PushUtil;
import com.jerryhumor.user.model.User;
import com.jerryhumor.util.JsonBuilder;
import com.jerryhumor.util.TimeUtil;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.util.HashMap;
import java.util.List;

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
            String sqlExceptSelect = "from news ";
            sqlExceptSelect +=
                    "where unix_timestamp(create_time) <= unix_timestamp('" + TimeUtil.getTimeFormatted(firstNewsCreateTime, TimeUtil.FORMAT_DEFAULT) + "')";
            if (newsType != NewsType.CODE_LATEST){
                sqlExceptSelect += " and category = " + newsType;
            }
            System.out.println(sqlExceptSelect);
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
        int articleId = getParaToInt(0);
        int newsType = getParaToInt(1);
        String userAccount = getPara(2);
        if (userAccount != null){
            //更新用户喜好
            String sqlSelect = "select * from user where account = '" + userAccount + "'";
            List<User> userList = User.dao.find(sqlSelect);
            if (userList != null && userList.size() > 0){
                User selectedUser = userList.get(0);
                String updatedFavour = generateUpdatedFavour(selectedUser.getFavour(), newsType);
                selectedUser.setFavour(updatedFavour);
                selectedUser.setMostFavour(getMostFavour(updatedFavour));
                selectedUser.update();
            }
        }
        //获取文章
        Article article = Article.dao.findById(articleId);
        renderText(JsonBuilder.generateArticleJson(article));
    }

    public void push(){
        List<News> newsList = News.dao.find("select * from news where category = 1");
        if (newsList == null || newsList.size() == 0){
            System.out.println("没有1类型的新闻");
        }else{
            PushUtil.pushNewsInfo(newsList.get(0));
        }
        renderText("完成操作");
    }

    private String generateErrorJson(String error){
        return "{\"status\":\"failed\", \"error\":\"" + error + "\"}";
    }

    /**
     * 生成更新之后的用户喜好字段
     * @param favour
     * @param newsType
     * @return
     */
    private String generateUpdatedFavour(final String favour, final int newsType){
        String[] favourArray = favour.split("-");
        String selectedFavourStr = favourArray[newsType - 1];
        int selectedFavourInt = Integer.parseInt(selectedFavourStr) + 1;
        String updatedFavour = "";
        for (int i = 0; i < favourArray.length; i++){
            if (i != newsType - 1){
                updatedFavour += favourArray[i];
            }else{
                updatedFavour += selectedFavourInt + "";
            }
            if (i != 9)
                updatedFavour += "-";
        }
        System.out.println("更新用户字号字段，字段：" + updatedFavour);
        return updatedFavour;
    }

    /**
     * 从用户喜好字段中获取比重最大的新闻类型
     * @param favour
     * @return
     */
    private int getMostFavour(final String favour){
        String[] favourArray = favour.split("-");
        int mostFavour = 1, type = 1;
        for (int i = 0; i < favourArray.length; i++){
            int f = Integer.parseInt(favourArray[i]);
            if (f > mostFavour){
                mostFavour = f;
                type = i + 1;
            }
        }
        return type;
    }







}
