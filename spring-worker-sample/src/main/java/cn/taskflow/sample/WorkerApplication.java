package cn.taskflow.sample;

import cn.feiliu.taskflow.common.utils.TaskflowUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class WorkerApplication {

    public static void main(String[] args) {
        SpringApplication.run(WorkerApplication.class, args);
    }

    @Bean
    public ApplicationListener<ContextStartedEvent> bindEvent(Environment environment) {
        return (event) -> {
            String port = environment.getProperty("server.port");
            System.out.println(TaskflowUtils.f("系统启动成功: http://localhost:%sd/", port));
        };
    }
}
