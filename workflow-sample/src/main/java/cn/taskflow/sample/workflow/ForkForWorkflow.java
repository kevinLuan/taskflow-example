package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import static cn.feiliu.taskflow.expression.Expr.*;

import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.ForkFor;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
@Service
public class ForkForWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-fork-for-workflow";
    @Getter
    private int version = 1;

    @Override
    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                // 加法计算任务
                .addTask(new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b")))
                // for循环任务 (遍历 elements)
                .addTask(new ForkFor("forRef", workflow().input.get("elements"))
                        // 减法计算任务(使用循环的element和index)
                        .childTask(new WorkTask("subtract", "subtractRef")
                                .input(Pair.of("a").fromTaskOutput("forRef", "element"))
                                .input(Pair.of("b").fromTaskOutput("forRef", "index")))
                        // 乘法计算任务
                        .childTask(
                                new WorkTask("multiply", "multiplyRef")
                                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                        .input(Pair.of("b").fromTaskOutput("subtractRef", "result"))
                        )
                ).addTask(new WorkTask("divide", "divideRef")
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input("b", "2")
                ).build();
        return apiClient.getWorkflowDefClient().registerWorkflow(workflowDef, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        dataMap.put("elements", Lists.newArrayList(10, 20, 30, 40, 50));
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
