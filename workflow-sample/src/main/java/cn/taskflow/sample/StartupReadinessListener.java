package cn.taskflow.sample;

import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.sdk.workflow.def.ValidationError;
import cn.taskflow.sample.workflow.CustomWorkflow;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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
        registerWorkflows();
        List<CompletableFuture<ExecutingWorkflow>> futures = runWorkflows();
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((r, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
        asyncWaitingForTimeout(future);
    }

    private List<CompletableFuture<ExecutingWorkflow>> runWorkflows() {
        List<CompletableFuture<ExecutingWorkflow>> futures = new ArrayList<>();
        log.info("====================RunWorkflow====================");
        for (CustomWorkflow workflow : workflows) {
            if (workflow.getName().equals("simple-do-while-workflow")) {
                log.info("Workflow run start: {}", workflow.getName());
                CompletableFuture<ExecutingWorkflow> future = workflow.run();
                futures.add(future.whenComplete((r, e) -> {
                    if (r != null) {
                        log.info("Workflow execution done, name: `{}` workflowId: {}", workflow.getName(), r.getWorkflowId());
                    } else {
                        log.error("Workflow execution error, name: `{}` error:{}", workflow.getName(), e);
                    }
                }));
            }
        }
        log.info("====================RunWorkflow Done====================");
        return futures;
    }

    /**
     * 注册工作流定义
     */
    private void registerWorkflows() {
        log.info("====================register workflow====================");
        log.info("Workflow register start, count: {}", workflows.size());
        for (CustomWorkflow workflow : workflows) {
            log.info("Workflow register start, name: {}, version:{}", workflow.getName(), workflow.getVersion());
            try {
                boolean created = workflow.register();
                String name = workflow.getName();
                int version = workflow.getVersion();
                if (created) {
                    log.info("The registration workflow definition, name: `{}` version: {}, result: {}", name, version, created);
                } else {
                    throw new IllegalStateException("注册工作流失败,name:" + name);
                }
            }catch (ValidationError e){
                log.error("-----------------------------------");
                log.error("Workflow register error, name: `{}`", workflow.getName());
                for (String error : e.getErrors()) {
                    log.error(error);
                }
                log.error("-----------------------------------");
                throw e;
            }
        }
        log.info("====================register done====================");
    }

    /**
     * 仅在 debugger 模式下
     */
    private static void asyncWaitingForTimeout(CompletableFuture<Void> future) {
        CompletableFuture.runAsync(() -> {
            try {
                future.get(5, TimeUnit.MINUTES);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
