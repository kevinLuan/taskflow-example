package cn.taskflow.sample;

import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.taskflow.sample.workflow.CustomWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
@Slf4j
@Service
public class StartupReadinessListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private List<CustomWorkflow> workflows = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("ApplicationReadyEvent workflows count:{}", workflows.size());
        for (CustomWorkflow workflow : workflows) {
            boolean created = workflow.register();
            String name = workflow.getName();
            int version = workflow.getVersion();
            log.info("The registration workflow definition, name: `{}` version: {}, result: {}", name, version, created);
        }
        List<CompletableFuture<ExecutingWorkflow>> futures = new ArrayList<>();
        for (CustomWorkflow workflow : workflows) {
            String name = workflow.getName();
            int version = workflow.getVersion();
            CompletableFuture<ExecutingWorkflow> future = workflow.run();
            futures.add(future.whenComplete((r, e) -> {
                if (r != null) {
                    log.info("Workflow execution done, name: `{}` workflowId: {}", name,r.getWorkflowId());
                } else {
                    log.error("Workflow execution error, name: `{}` error:{}", name, e);
                }
            }));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((r, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
    }
}
