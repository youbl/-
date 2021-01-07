package com.beinet.resourcecapture.captureTask;

import com.beinet.resourcecapture.captureTask.services.CaptureInterface;
import com.beinet.resourcecapture.captureTask.services.CaptureKanMeiNv;
import com.beinet.resourcecapture.captureTask.services.CaptureXHerNet;
import com.beinet.resourcecapture.captureTask.services.DownImgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CaptureTask {
    @Autowired
    CaptureKanMeiNv captureKanMeiNv;

    @Autowired
    CaptureXHerNet captureXHerNet;

    @Bean
    public TaskScheduler taskScheduler() {
        // 方法加上Async后，就可以多线程启动任务了
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        return scheduler;
    }

    /**
     * 一个任务启动后，下一次一定要等当前任务完毕
     */
   // @Scheduled(cron = "* * * * * *")
    @Async
    void captureImg() {
        println("抓取开始。。。");
//        try {
//            Thread.sleep(50000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        captureXHerNet.begin();
        println("抓取结束。");
    }

    @Scheduled(cron = "* * * * * *")
    @Async
    void downImg() throws IOException {
        println("下载开始。。。");
        new DownImgs("d:\\mine\\JavaProject\\resourceCapture\\txt\\xher\\").execute();
        println("下载结束。");
    }

//    @Scheduled(cron = "* * * * * *")
//    void test() {
//        System.out.println("开始。。。");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("结束。。。");
//    }
//
//    @Scheduled(cron = "* * * * * *")
//    void test2() {
//        System.out.println("开始2。。。");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("结束2。。。");
//    }

    public static void println(String msg) {
        System.out.print(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        System.out.print(" " + Thread.currentThread().getId());
        System.out.println(" " + msg);
    }
}
