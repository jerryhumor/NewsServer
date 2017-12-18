package com.jerryhumor.spider;

import java.util.TimerTask;

public class NeteaseSpiderTask extends TimerTask {
    public void run() {
        NeteaseSpider spider = new NeteaseSpider();
        spider.startGather();
    }
}
