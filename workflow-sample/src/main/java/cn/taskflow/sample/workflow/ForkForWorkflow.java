package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.ForkFor;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.SimpleTask;
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
public class ForkForWorkflow implements CustomWorkflow {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-fork-for-workflow";
    @Getter
    private int version = 1;

    @Override
    public boolean register() {
        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(name, version)
                .add(new SimpleTask("add", "addRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"))
                .add(new ForkFor("forRef", "${workflow.input.elements}")
                        .loopOver(
                                new SimpleTask("subtract", "subtractRef")
                                        .input("a", "${forRef.output.element}")
                                        .input("b", "${forRef.output.index}"),
                                new SimpleTask("multiply", "multiplyRef")
                                        .input("a", "${addRef.output.sum}")
                                        .input("b", "${subtractRef.output.result}")
                        )).add(new SimpleTask("divide", "divideRef")
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
        dataMap.put("elements", Lists.newArrayList(10, 20, 30, 40, 50));
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
