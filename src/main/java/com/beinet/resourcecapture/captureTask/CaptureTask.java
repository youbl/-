package com.beinet.resourcecapture.captureTask;

import com.beinet.resourcecapture.captureTask.services.CaptureInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class CaptureTask {
    @Autowired
    CaptureInterface captureService;

    /**
     * 一个任务启动后，下一次一定要等当前任务完毕
     */
    @Scheduled(cron = "* * * * * *")
    void captureImg() {
        println("抓取开始。。。");
        captureService.begin();
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
