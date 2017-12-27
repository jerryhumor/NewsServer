package com.jerryhumor.spider.bean;

import com.google.gson.annotations.SerializedName;

public class ZhihuArticle {

    @SerializedName("body")
    private String body;
    @SerializedName("title")
    private String title;

    public String getBody() {
        return body;
    }

    public String getTitle() {
        return title;
    }
}
