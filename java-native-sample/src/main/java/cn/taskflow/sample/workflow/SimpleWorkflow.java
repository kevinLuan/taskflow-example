package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.StartWorkflowRequest;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.For;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import com.google.common.collect.Lists;

import java.util.HashMap;
import java.util.Map;

import static cn.feiliu.taskflow.expression.Expr.workflow;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-04
 */
public class SimpleWorkflow implements IWorkflowService {
    private ApiClient apiClient;
    private String name = "simple-workflow";
    private int version = 1;

    public SimpleWorkflow(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                .addTask(new WorkTask("echo", "echoRef")
                        .input(Pair.of("value").fromWorkflow("message")))
                // 加法计算任务
                .addTask(new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b")))
                // for循环(遍历elements)
                .addTask(new For("forRef", workflow().input.get("elements"))
                        // 减法计算任务(输入来自for循环的element和index)
                        .childTask(new WorkTask("subtract", "subtractRef")
                                .input(Pair.of("a").fromTaskOutput("forRef", "element"))
                                .input(Pair.of("b").fromTaskOutput("forRef", "index")))
                        // 乘法计算任务
                        .childTask(new WorkTask("multiply", "multiplyRef")
                                .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                .input(Pair.of("b").fromTaskOutput("subtractRef", "result"))
                        )).addTask(new WorkTask("divide", "divideRef")
                        // 除法计算任务
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input("b", 2)
                ).build();
        return apiClient.getApis().getWorkflowEngine().registerWorkflow(workflowDef, true);
    }

    public String runWorkflow() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        dataMap.put("message", "Hello World");
        dataMap.put("elements", Lists.newArrayList(10, 20, 30, 40, 50));
        StartWorkflowRequest req = StartWorkflowRequest.newBuilder().name(name).version(version).input(dataMap).build();
        return apiClient.getApis().getWorkflowClient().startWorkflow(req);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getVersion() {
        return version;
    }
}
