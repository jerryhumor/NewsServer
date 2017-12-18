package com.jerryhumor.config;

import com.jerryhumor.classifier.ArticleClassifier;
import com.jerryhumor.news.controller.NewsController;
import com.jerryhumor.segment.CoreNLPSegment;
import com.jerryhumor.spider.NeteaseSpider;
import com.jerryhumor.util.MappingKit;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.Engine;

public class ServerConfig extends JFinalConfig {

    public void configConstant(Constants constants) {
        PropKit.use("server_config.txt");
        constants.setDevMode(PropKit.getBoolean("devMode", false));
    }

    public void configRoute(Routes routes) {
        routes.add("/", NewsController.class);
    }

    public void configEngine(Engine engine) { }

    public void configPlugin(Plugins plugins) {
        DruidPlugin druidPlugin = new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
        plugins.add(druidPlugin);

        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        MappingKit.mapping(arp);
        plugins.add(arp);
    }

    public void configInterceptor(Interceptors interceptors) { }

    public void configHandler(Handlers handlers) { }

    public static DruidPlugin createDruidPlugin() {
        return new DruidPlugin(PropKit.get("jdbcUrl"), PropKit.get("user"), PropKit.get("password").trim());
    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();

        //初始化分词器
        CoreNLPSegment.init();
        //初始化分类器
        ArticleClassifier.init();
        //启动网易爬虫
        NeteaseSpider.start();
    }
}
