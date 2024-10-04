package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.StartWorkflowRequest;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.model.WorkflowRun;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import static cn.feiliu.taskflow.expression.Expr.*;

import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.Dynamic;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.For;
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
public class SimpleDynamicTaskWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-dynamic-workflow";
    @Getter
    private int version = 2;

    @Override
    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                // 获取订单列表任务
                .addTask(new WorkTask("getOrders", "getOrdersRef"))
                // 循环遍历任务
                .addTask(new For("orderRef", task("getOrdersRef").output.get("result"))
                        .childTask(new WorkTask("expressDelivery", "expressDeliveryRef")
                                .input(Pair.of("order").fromTaskOutput("orderRef", "element")))
                        .childTask(new Dynamic("dynamicExpressDeliveryRef", task("expressDeliveryRef").output.get("expressType"))
                                .input(Pair.of("delivery").fromTaskOutput("expressDeliveryRef"))
                        )
                ).build();
        return apiClient.getApis().getWorkflowEngine().registerWorkflow(workflowDef, true);
    }

    @Override
    public String runWorkflow() {
        Map<String, Object> dataMap = new HashMap<>();
        StartWorkflowRequest req = StartWorkflowRequest.newBuilder().name(name).version(version).input(dataMap).build();
        return apiClient.getApis().getWorkflowClient().startWorkflow(req);
    }
}
