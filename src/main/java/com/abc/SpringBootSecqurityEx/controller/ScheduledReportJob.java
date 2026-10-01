package com.abc.SpringBootSecqurityEx.controller;


import com.abc.SpringBootSecqurityEx.service.TestService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Component
public class ScheduledReportJob {

    @Autowired
    private TestService emailService;

//
// Runs every 10 seconds just for demo (change later to daily)
//    @Scheduled(fixedRate = 1000)
//    public void sendReports() {
//        System.out.println("⏱ Scheduler triggered on thread: " + Thread.currentThread().getName());
//        emailService.sendDailyReportEmail("manager@restaurant.com");
//        emailService.sendDailyReportEmail("owner@restaurant.com");
//    }
//
//    @Scheduled(fixedRate = 100) // every 5 seconds
//    public void fixedRateTask() {
//        System.out.println("Fixed rate task executed at " + LocalDateTime.now());
//        try {
//            Thread.sleep(3000); // simulate long-running task
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Scheduled(fixedDelay = 1000) // 5 sec after previous finishes
//    public void fixedDelayTask() throws InterruptedException {
//        System.out.println("Fixed delay task started at " + LocalDateTime.now());
//        Thread.sleep(3000); // simulate long-running task
//        System.out.println("Fixed delay task finished at " + LocalDateTime.now());
//    }
//
//    @Scheduled(fixedRate = 10000, initialDelay = 15000) // start after 15 sec
//    public void delayedStartTask() {
//        System.out.println("Task started after initial delay at " + LocalDateTime.now());
//    }
//    // every minute at second 0
//    @Scheduled(cron = "0 0 14 0 0 *")
//    public void cronTask() {
//        System.out.println("Cron task executed at " + LocalDateTime.now());
//    }
//
//    // every day at 9:30 AM
//    @Scheduled(cron = "0 30 9 * * *")
//    public void dailyTask() {
//        System.out.println("Daily task executed at " + LocalDateTime.now());
//    }
//
//    // every Monday at 10 AM
//    @Scheduled(cron = "0 0 10 * * MON")
//    public void weeklyTask() {
//        System.out.println("Weekly task executed at " + LocalDateTime.now());
//    }
//
//
//    @Async
//    @Scheduled(fixedRate = 10000)
//    public void asyncTask() throws InterruptedException {
//        System.out.println("Async task started at " + LocalDateTime.now() + " on thread " + Thread.currentThread().getName());
//        Thread.sleep(5000); // simulate long task
//        System.out.println("Async task finished at " + LocalDateTime.now() + " on thread " + Thread.currentThread().getName());
//    }
}
