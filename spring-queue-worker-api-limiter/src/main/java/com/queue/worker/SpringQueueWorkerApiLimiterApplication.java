package com.queue.worker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class SpringQueueWorkerApiLimiterApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringQueueWorkerApiLimiterApplication.class, args);
	}

}
