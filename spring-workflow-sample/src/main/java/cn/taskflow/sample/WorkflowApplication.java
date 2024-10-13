package cn.taskflow.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class WorkflowApplication {
    private static Logger log = LoggerFactory.getLogger(WorkflowApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(WorkflowApplication.class, args);
    }

    @Bean
    public ApplicationListener<ContextStartedEvent> bindEvent(Environment environment) {
        return (event) -> {
            log.info("The system started successfully.");
        };
    }
}
