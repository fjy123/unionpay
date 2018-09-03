package com.xiaocunzhe.unionpay;

import com.xiaocunzhe.unionpay.config.StartupRunner;
import com.xiaocunzhe.unionpay.config.TaskRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UnionpayApplication {

    public static void main(String[] args) {
        SpringApplication.run(UnionpayApplication.class, args);
    }

    @Bean
    public StartupRunner startupRunner(){
        return new StartupRunner();
    }

//    @Bean
//    public TaskRunner taskRunner(){
//        return new TaskRunner();
//    }
}
