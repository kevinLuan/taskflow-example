package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.SimpleTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
@Slf4j
@Service
public class SimpleWorkflow {
    @Autowired
    private ApiClient apiClient;
    private String workflowName = "simple-calculator-workflow";
    private int version = 1;

    @PostConstruct
    public void createWorkflow() {
        //创建工作流
        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(workflowName, version)
                .add(new SimpleTask("add", "addRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"))
                .add(new SimpleTask("subtract", "subtractRef")
                        .input("a", "${addRef.output.sum}")
                        .input("b", 10))
                .add(new SimpleTask("multiply", "multiplyRef")
                        .input("a", "${addRef.output.sum}")
                        .input("b", "${subtractRef.output.result}"))
                .add(new SimpleTask("divide", "divideRef")
                        .input("a", "${multiplyRef.output.result}")
                        .input("b", "2"))
                .build();
        //注册工作流定义
        log.info("Register the '{}' workflow definition, result:{}", workflow.getName(), workflow.registerWorkflow());
        run();
    }

    public void run() {
        boolean published = apiClient.getWorkflowDefClient().publishWorkflowDef(workflowName, version, false);
        log.info("发布工作流：{}", published);
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        try {
            CompletableFuture<ExecutingWorkflow> future = apiClient.getWorkflowExecutor().executeWorkflow(workflowName, version, dataMap);
            future.thenAccept((w) -> {
                System.out.println("workflowName: " + w.getWorkflowName() + ", workflowId: " + w.getWorkflowId());
            }).join();
            System.out.println("done");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
