package com.jerryhumor.spider;

import java.util.TimerTask;

public class SpiderTask extends TimerTask {

    private static int count = 0;

    public void run() {
        switch (++count){
            case 1:
                startZhihuCollector();
                break;
            case 2:
                startZhihuCollector();
                break;
            case 3:
                startZhihuCollector();
                break;
            case 4:
                count = 0;
                startZhihuCollector();
            default:
                count = 0;
                break;
        }
    }

    private void startNeteaseSpider(){
        new NeteaseSpider().startGather();
    }

    private void startSinaSpider(){
        new SinaSpider().startGather();
    }

    private void startZhihuCollector(){
        new ZhihuCollector().startGather();
    }
}
