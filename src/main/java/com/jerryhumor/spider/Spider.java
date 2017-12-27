package com.jerryhumor.spider;

import com.jerryhumor.spider.constant.SpiderConstant;

import java.util.Timer;

public class Spider {

    public static void start(){
        Timer spiderTimer = new Timer();
        spiderTimer.schedule(new SpiderTask(), SpiderConstant.PERIOD, SpiderConstant.PERIOD);
    }
}
