package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.StartWorkflowRequest;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.model.WorkflowRun;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.ForkJoin;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.Task;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 数据定义示例：
 * 执行示例：
 * <pre>
 * -----------------------------
 * |          fork-join        |
 * -----------------------------
 * |             |             |
 * add           multiply      echo
 * |             |             |
 * subtract      divide        |
 * |             |             |
 * -----------------------------
 * |           join            |
 * -----------------------------
 * </pre>
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-08
 */
@Service
public class ForkWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-fork-workflow";
    @Getter
    private int version = 1;

    @Override
    /**
     * 注册工作流
     * @return 是否成功注册
     */
    public boolean register() {
        Task[][] tasks = new Task[3][];
        // 第一组任务
        tasks[0] = new Task[]{
                // 加法计算任务
                new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b")),
                // 减法计算任务
                new WorkTask("subtract", "subtractRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b"))
        };
        // 第二组任务
        tasks[1] = new Task[]{
                // 乘法计算任务
                new WorkTask("multiply", "multiplyRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b")),
                // 除法计算任务
                new WorkTask("divide", "divideRef")
                        .input(Pair.of("a").fromWorkflow("a"))
                        .input(Pair.of("b").fromWorkflow("b"))
        };
        // 第三组任务
        tasks[2] = new Task[]{new WorkTask("echo", "echoRef")
                .input(Pair.of("value").fromWorkflow("msg"))};

        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                .addTask(new ForkJoin("forkRef", tasks))
                .build();

        return apiClient.getApis().getWorkflowEngine().registerWorkflow(workflowDef, true);
    }

    @Override
    public String runWorkflow() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        dataMap.put("msg", "Hello World!");
        StartWorkflowRequest req = StartWorkflowRequest.newBuilder().name(name).version(version).input(dataMap).build();
        return apiClient.getApis().getWorkflowClient().startWorkflow(req);
    }
}
