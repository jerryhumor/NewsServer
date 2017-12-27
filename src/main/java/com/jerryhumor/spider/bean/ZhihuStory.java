package com.jerryhumor.spider.bean;

import com.google.gson.annotations.SerializedName;

public class ZhihuStory {

    @SerializedName("id")
    private int newsId;
    @SerializedName("title")
    private String title;

    public int getNewsId() {
        return newsId;
    }

    public String getTitle() {
        return title;
    }
}
