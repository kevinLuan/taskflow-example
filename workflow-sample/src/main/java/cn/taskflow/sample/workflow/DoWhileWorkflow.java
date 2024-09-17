package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.StartWorkflowRequest;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.model.WorkflowRun;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import static cn.feiliu.taskflow.expression.Expr.*;

import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.DoWhile;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
@Service
public class DoWhileWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-do-while-workflow";
    @Getter
    private int version = 1;

    @Override
    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                // 加法计算任务
                .addTask(new WorkTask("add", "addRef")
                        .input(Pair.of("a").fromWorkflow("numA"))
                        .input(Pair.of("b").fromWorkflow("numB")))
                //do-while循环
                .addTask(new DoWhile("doWhile1Ref", 1)
                        // 减法计算任务
                        .childTask(new WorkTask("subtract", "subtract1Ref")
                                .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                .input("b", 1))
                        // 乘法计算任务
                        .childTask(new WorkTask("multiply", "multiply1Ref")
                                .input(Pair.of("a").fromTaskOutput("subtract1Ref", "result"))
                                .input("b", 2)
                        ))
                //do-while循环
                .addTask(new DoWhile("doWhile2Ref", workflow().input.get("loopCount"))
                        // 减法计算任务
                        .childTask(new WorkTask("subtract", "subtract2Ref")
                                .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                                .input(Pair.of("b").fromWorkflow("numB")))
                        // 乘法计算任务
                        .childTask(new WorkTask("multiply", "multiply2Ref")
                                .input(Pair.of("a").fromTaskOutput("subtract2Ref", "result"))
                                .input("b", 2)
                        ))
                // 除法计算任务
                .addTask(new WorkTask("divide", "divideRef")
                        .input(Pair.of("a").fromTaskOutput("addRef", "sum"))
                        .input("b", "2")
                ).build();
        return apiClient.getWorkflowEngine().registerWorkflow(workflowDef, true);
    }

    @Override
    public String runWorkflow() {
        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("numA", 100);
        dataMap.put("numB", 200);
        dataMap.put("loopCount", 3);
        StartWorkflowRequest req = StartWorkflowRequest.newBuilder().name(name).version(version).input(dataMap).build();
        return apiClient.getWorkflowClient().startWorkflow(req);
    }
}
