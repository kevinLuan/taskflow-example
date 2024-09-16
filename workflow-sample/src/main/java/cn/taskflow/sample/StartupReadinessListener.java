package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.model.WorkflowRun;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.sdk.workflow.def.ValidationException;
import cn.taskflow.sample.workflow.IWorkflowService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
@Slf4j
@Service
public class StartupReadinessListener implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private List<IWorkflowService> workflows = new ArrayList<>();
    @Autowired
    private ApiClient apiClient;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        registerWorkflows();
        List<CompletableFuture<ExecutingWorkflow>> futures = runWorkflows();
        CompletableFuture<Void> future = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).whenComplete((r, e) -> {
            if (e != null) {
                e.printStackTrace();
            }
        });
        future.join();
//        asyncWaitingForTimeout(future);
    }

    private List<CompletableFuture<ExecutingWorkflow>> runWorkflows() {
        List<CompletableFuture<ExecutingWorkflow>> futures = new ArrayList<>();
        log.info("====================RunWorkflow====================");
        for (IWorkflowService workflow : workflows) {
            log.info("Workflow run start: {}", workflow.getName());
            String workflowId = workflow.runWorkflow();
            futures.add(CompletableFuture.supplyAsync(() -> {
                return waitForTerminal(workflowId, 30);
            }).whenComplete((r, e) -> {
                if (r != null) {
                    log.info("Workflow execution name: `{}` workflowId: {}, status:{}", workflow.getName(), r.getWorkflowId(), r.getStatus());
                } else {
                    log.error("Workflow execution error, name: `{}` error:{}", workflow.getName(), e);
                }
            }));
        }
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.info("====================RunWorkflow Done====================");
        return futures;
    }

    @SneakyThrows
    public ExecutingWorkflow waitForTerminal(String workflowId, int waitForSeconds) {
        long startTime = System.currentTimeMillis();
        for (; ; ) {
            ExecutingWorkflow workflow = apiClient.getWorkflowClient().getWorkflow(workflowId, true);
            if (workflow.getStatus().isTerminal()) {
                return workflow;
            } else {
                long cost = System.currentTimeMillis() - startTime;
                if (cost >= waitForSeconds * 1000) {
                    throw new TimeoutException("Timeout exceeded while waiting for workflow to reach terminal state.");
                }
                long remaining = waitForSeconds * 1000 - cost;
                Thread.sleep(Math.min(100, remaining));
            }
        }
    }

    /**
     * 注册工作流定义
     */
    private void registerWorkflows() {
        log.info("====================register workflow====================");
        log.info("Workflow register start, count: {}", workflows.size());
        for (IWorkflowService workflow : workflows) {
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
            } catch (ValidationException e) {
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
