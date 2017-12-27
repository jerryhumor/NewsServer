package com.jerryhumor.spider.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ZhihuListResponse {

    @SerializedName("date")
    private String date;
    @SerializedName("stories")
    List<ZhihuStory> stories;

    public List<ZhihuStory> getStories() {
        return stories;
    }
}
