package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
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
    public boolean register() {
        Task[][] tasks = new Task[3][];
        tasks[0] = new Task[]{
                new WorkTask("add", "addRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"),
                new WorkTask("subtract", "subtractRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}")
        };
        tasks[1] = new Task[]{
                new WorkTask("multiply", "multiplyRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}"),
                new WorkTask("divide", "divideRef")
                        .input("a", "${workflow.input.a}")
                        .input("b", "${workflow.input.b}")
        };
        tasks[2] = new Task[]{new WorkTask("echo", "echoRef")
                .input("value", "${workflow.input.msg}")};

        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(name, version)
                .add(new ForkJoin("forkRef", tasks)).build();
        return workflow.registerWorkflow(true, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("a", 100);
        dataMap.put("b", 200);
        dataMap.put("msg", "Hello World!");
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, dataMap);
    }
}
