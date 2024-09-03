package cn.taskflow.sample;

import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.taskflow.sample.workflow.IWorkflow;
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
    private List<IWorkflow> workflows = new ArrayList<>();

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("ApplicationReadyEvent workflows count:{}", workflows.size());
        for (IWorkflow workflow : workflows) {
            boolean created = workflow.createWorkflow();
            //注册工作流定义
            log.info("Register the '{}' workflow definition, result:{}", workflow.getName(), created);
            CompletableFuture<ExecutingWorkflow> future = workflow.run();
            future.thenAccept((w) -> {
                System.out.println("workflowName: " + w.getWorkflowName() + ", workflowId: " + w.getWorkflowId());
            }).join();
            System.out.println("done");
        }
    }
}
