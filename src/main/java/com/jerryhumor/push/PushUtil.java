package com.jerryhumor.push;

import com.gexin.rp.sdk.base.IPushResult;
import com.gexin.rp.sdk.base.impl.ListMessage;
import com.gexin.rp.sdk.base.impl.Target;
import com.gexin.rp.sdk.http.IGtPush;
import com.gexin.rp.sdk.template.TransmissionTemplate;

import com.jerryhumor.news.model.News;
import com.jerryhumor.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class PushUtil {
    //定义常量, appId、appKey、masterSecret 采用本文档 "第二步 获取访问凭证 "中获得的应用配置
    private static String appId = "DDxuerVhkWAqC29zxvgxF8";
    private static String appKey = "k2lwGZgHof7VVz67EuG11A";
    private static String masterSecret = "TZEEjxdPjG9hItmukD8aE5";
    private static String url = "http://sdk.open.api.igexin.com/apiex.htm";

    /**
     * 根据新闻类型，将新闻发送给喜欢该种类型的用户
     * @param news
     */
    public static void pushNewsInfo(News news){
        System.out.println("开始推送新闻，新闻类型：" + news.getCategory());
        List<Target> targets = generateTargetList(news.getCategory());
        TransmissionTemplate template = generateTemplate(news);
        pushNewsInfo(targets, template);
    }

    /**
     * 向目标列表发送消息
     * @param targets
     * @param template
     */
    public static void pushNewsInfo(List<Target> targets, TransmissionTemplate template){
        IGtPush push = new IGtPush(url, appKey, masterSecret);
        ListMessage message = new ListMessage();
        message.setData(template);
        message.setOffline(true);
        message.setOfflineExpireTime(10 * 60 * 1000);
        String taskId = push.getContentId(message);
        IPushResult ret = push.pushMessageToList(taskId, targets);
        System.out.println(ret.getResponse().toString());
    }

    /**
     * 根据新闻类型来生成最喜欢这种类型新闻的用户目标的列表
     * @param newsType
     * @return
     */
    private static List<Target> generateTargetList(int newsType){
        System.out.println("生成目标列表，新闻类型：" + newsType);
        List<Target> targetList = null;
        List<String> accountList = getAccountListBy(newsType);
        if (accountList == null || accountList.size() <= 0){
            System.out.println("生成目标列表，没有该类型的用户");
        } else {
            System.out.println("生成目标列表，获取到该新闻类型的用户数量：" + accountList.size());
            targetList = new ArrayList<Target>();
            for (String account : accountList){
                Target target = new Target();
                target.setAppId(appId);
                target.setAlias(account);
                targetList.add(target);
            }
        }
        return targetList;
    }

    /**
     * 根据新闻类别来找到最喜欢这种类别的用户的账号
     * @param mostLikeType
     * @return
     */
    private static List<String> getAccountListBy(int mostLikeType){
        System.out.println("获取用户账号列表，新闻类型：" + mostLikeType);
        String sql = "select * from user where most_favour = " + mostLikeType;
        System.out.println("查询语句：" + sql);
        List<User> userList = User.dao.find(sql);
        List<String> accountList = null;
        if (userList == null || userList.size() == 0){
            System.out.println("获取用户账号列表，该类型用户不存在");
        }else{
            System.out.println("获取用户账号列表，该类型用户数量：" + userList.size());
            accountList = new ArrayList<String>();
            for (User user : userList){
                accountList.add(user.getAccount());
            }
        }
        return accountList;
    }

    /**
     * 生成透传通知
     * @param jsonInfo              要发送给客户端的json数据
     * @return
     */
    private static TransmissionTemplate generateTemplate(String jsonInfo){
        System.out.println("生成透传模板，json数据：" + jsonInfo);
        TransmissionTemplate template = new TransmissionTemplate();
        template.setAppId(appId);
        template.setAppkey(appKey);
        // 透传消息设置，1为强制启动应用，客户端接收到消息后就会立即启动应用；2为等待应用启动
        template.setTransmissionType(2);
        template.setTransmissionContent(jsonInfo);
        return template;
    }

    /**
     * 将新闻封装成透传通知
     * @param news
     * @return
     */
    private static TransmissionTemplate generateTemplate(News news){
        return generateTemplate(generateNewsJson(news));
    }

    /**
     * 将新闻封装成json数据
     * @param news
     * @return
     */
    private static String generateNewsJson(News news){
        String json =
                "{" +
                "\"title\":\"" + news.getTitle() + "\"," +
                "\"news_id\":" + news.getId() + "," +
                "\"news_source\":" + news.getSource() +
                "}";
        return json;
    }
}
