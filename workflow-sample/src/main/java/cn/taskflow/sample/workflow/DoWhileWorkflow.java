package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.DoWhile;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.SimpleTask;
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
        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(name, version)
                .add(new SimpleTask("add", "addRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"))
                .add(new DoWhile("doWhile1Ref", 1)
                        .loopOver(
                                new SimpleTask("subtract", "subtract1Ref")
                                        .input("a", "${addRef.output.sum}")
                                        .input("b", "${addRef.output.sum}"),
                                new SimpleTask("multiply", "multiply1Ref")
                                        .input("a", "${addRef.output.sum}")
                                        .input("b", "${addRef.output.sum}")
                        ))
                .add(new DoWhile("doWhile2Ref", "${workflow.input.loopCount}")
                        .loopOver(
                                new SimpleTask("subtract", "subtract2Ref")
                                        .input("a", "${workflow.input.a}")
                                        .input("b", "${workflow.input.a}"),
                                new SimpleTask("multiply", "multiply2Ref")
                                        .input("a", "${workflow.input.b}")
                                        .input("b", "${workflow.input.b}")
                        ))
                .add(new SimpleTask("divide", "divideRef")
                        .input("a", "${addRef.output.sum}")
                        .input("b", "2")
                ).build();
        return workflow.registerWorkflow(true, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        dataMap.put("loopCount", 3);
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
