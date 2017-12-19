package com.jerryhumor.util;

import com.jerryhumor.news.model.Article;
import com.jerryhumor.news.model.News;
import com.jerryhumor.user.model.User;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * 配置数据库表 建立数据库连接
 */
public class MappingKit {

    public static void mapping(ActiveRecordPlugin arp) {
        arp.addMapping("article", "id", Article.class);
        arp.addMapping("news", "id", News.class);
        arp.addMapping("user", "id", User.class);
    }
}
