package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
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
public class SimpleWorkflow implements CustomWorkflow {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-calculator-workflow";
    @Getter
    private int version = 1;

    @Override
    public boolean register() {
        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(name, version)
                .add(new WorkTask("add", "addRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"))
                .add(new WorkTask("subtract", "subtractRef")
                        .input("a", "${addRef.output.sum}")
                        .input("b", 10))
                .add(new WorkTask("multiply", "multiplyRef")
                        .input("a", "${addRef.output.sum}")
                        .input("b", "${subtractRef.output.result}"))
                .add(new WorkTask("divide", "divideRef")
                        .input("a", "${multiplyRef.output.result}")
                        .input("b", "2"))
                .build();
        return workflow.registerWorkflow(true, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
