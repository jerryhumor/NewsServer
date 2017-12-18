package com.jerryhumor.util;

import com.jerryhumor.news.constant.NewsSource;
import com.jerryhumor.news.constant.NewsType;

/**
 * Created by hanjianhao on 2017/12/11.
 *
 * 用于转换一些东西 如新闻类型等等
 */

public class Transformer {

    /**
     * 根据新闻碎片类型获取url
     * @param fragmentType
     * @return
     */
    public static String getUrl(int fragmentType){
        //todo 编写逻辑 (考虑要不要)
        return null;
    }

    /**
     * 根据类型代号获取类型名称
     * @param type
     * @return
     */
    public static String getTypeName(int type){
        String name = null;
        switch (type){
            case NewsType.CODE_LATEST:
                name = NewsType.NAME_LATEST;
                break;
            case NewsType.CODE_TECH:
                name = NewsType.NAME_TECH;
                break;
            case NewsType.CODE_SPORT:
                name = NewsType.NAME_SPORT;
                break;
            case NewsType.CODE_SOCIAL:
                name = NewsType.NAME_SOCIAL;
                break;
            case NewsType.CODE_FUN:
                name = NewsType.NAME_FUN;
                break;
            case NewsType.CODE_CAR:
                name = NewsType.NAME_CAR;
                break;
            case NewsType.CODE_ART:
                name = NewsType.NAME_ART;
                break;
            case NewsType.CODE_FINANCE:
                name = NewsType.NAME_FINANCE;
                break;
            case NewsType.CODE_EDUCATE:
                name = NewsType.NAME_EDUCATE;
                break;
            case NewsType.CODE_WAR:
                name = NewsType.NAME_WAR;
                break;
            case NewsType.CODE_POLITICS:
                name = NewsType.NAME_POLITICS;
                break;
            default:
                name = NewsType.NAME_UNKNOWN;
                break;
        }
        return name;
    }

    /**
     * 根据新闻源代号获取新闻
     * @param source
     * @return
     */
    public static String getSourceName(int source){
        String name = null;
        switch (source){
            case NewsSource.CODE_NETEASE:
                name = NewsSource.NAME_NETEASE;
                break;
            case NewsSource.CODE_SINA:
                name = NewsSource.NAME_SINA;
                break;
            case NewsSource.CODE_ZHIHU:
                name = NewsSource.NAME_ZHIHU;
                break;
            default:
                name = NewsSource.NAME_UNKNOWN;
                break;
        }
        return name;
    }
}
