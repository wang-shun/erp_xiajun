package com.wangqin.globalshop.schedule.job;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TestJob {

    @Scheduled(cron = "0 0/1 * * * ? ")
    public void testJob(){
        System.out.println("========Test Job==========");
    }

}