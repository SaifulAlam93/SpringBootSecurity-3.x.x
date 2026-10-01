package com.abc.SpringBootSecqurityEx.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableAsync
@EnableScheduling
public class AsyncConfig {

//    @Bean(name = "taskExecutor")
//    public Executor taskExecutor() {
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(10);
//        executor.setMaxPoolSize(15);
//        executor.setQueueCapacity(20);
//        executor.setThreadNamePrefix("MyAsyncThread-");
//        executor.initialize();
//        return executor;
//    }
//
        @Bean(name = "taskExecutor")
        public Executor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

            // ✅ Fixed thread pool size
            executor.setCorePoolSize(15);
            executor.setMaxPoolSize(20);

            // ✅ Set a queue size (how many tasks can wait)
            executor.setQueueCapacity(20);

            // ✅ Thread name prefix (for logging)
            executor.setThreadNamePrefix("AsyncWorker------------");

            // ✅ Rejection policy: run in caller thread instead of throwing exception
            executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

            executor.initialize();
            return executor;
        }


    @Bean(name = "taskExecutor2")
    public Executor taskExecutor2() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        // ✅ Fixed thread pool size
        executor.setCorePoolSize(15);
        executor.setMaxPoolSize(20);

        // ✅ Set a queue size (how many tasks can wait)
        executor.setQueueCapacity(20);

        // ✅ Thread name prefix (for logging)
        executor.setThreadNamePrefix("AsyncWorker------2------");

        // ✅ Rejection policy: run in caller thread instead of throwing exception
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        executor.initialize();
        return executor;
    }


}