package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.expression.Expr;
import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.DoWhile;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
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
public class DoWhileWorkflow implements CustomWorkflow {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-do-while-workflow";
    @Getter
    private int version = 1;

    @Override
    public boolean register() {
        FeiLiuWorkflow<Map<String, Object>> feiLiuWorkflow = apiClient.newWorkflowBuilder(name, version)
                .add(new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("numA"))
                        .input(Pair.of("b").fromWorkflow("numB")))
                .add(new DoWhile("doWhile1Ref", 1)
                        .loopOver(
                                new WorkTask("subtract", "subtract1Ref")
                                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                        .input("b", 1),
                                new WorkTask("multiply", "multiply1Ref")
                                        .input(Pair.of("a").fromTaskOutput("subtract1Ref", "result"))
                                        .input("b", 2)
                        ))
                .add(new DoWhile("doWhile2Ref", Expr.workflow("loopCount"))
                        .loopOver(
                                new WorkTask("subtract", "subtract2Ref")
                                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                        .input(Pair.of("b").fromWorkflow("numB")),
                                new WorkTask("multiply", "multiply2Ref")
                                        .input(Pair.of("a").fromTaskOutput("subtract2Ref", "result"))
                                        .input("b", 2)
                        ))
                .add(new WorkTask("divide", "divideRef")
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input("b", "2")
                ).build();
        return feiLiuWorkflow.registerWorkflow(true, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("numA", 100);
        dataMap.put("numB", 200);
        dataMap.put("loopCount", 3);
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
