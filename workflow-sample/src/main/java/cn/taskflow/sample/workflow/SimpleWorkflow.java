package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
@Slf4j
@Service
public class SimpleWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-calculator-workflow";
    @Getter
    private int version = 1;

    @Override
    /**
     * 注册工作流
     * @return 是否成功注册
     */
    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                // 加法计算任务
                .addTask(new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b")))
                // 减法计算任务
                .addTask(new WorkTask("subtract", "subtractRef")
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input("b", 10))
                // 乘法计算任务
                .addTask(new WorkTask("multiply", "multiplyRef")
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input(Pair.of("b").fromTaskOutput("subtractRef", "result")))
                // 除法计算任务
                .addTask(new WorkTask("divide", "divideRef")
                        .input(Pair.of("a").fromTaskOutput("multiplyRef", "result"))
                        .input("b", 2))
                .build();
        return apiClient.getWorkflowDefClient().registerWorkflow(workflowDef, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
