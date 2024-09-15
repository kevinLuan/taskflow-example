package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.workflow.WorkflowDefinition;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import static cn.feiliu.taskflow.expression.Expr.*;

import cn.feiliu.taskflow.expression.Pair;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.Switch;
import cn.feiliu.taskflow.sdk.workflow.def.tasks.WorkTask;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-04
 */
@Slf4j
@Service
public class SwitchWorkflow implements IWorkflowService {
    @Autowired
    private ApiClient apiClient;
    @Getter
    private String name = "simple-switch-workflow";
    @Getter
    private int version = 1;

    /**
     * 定义一个简单的Switch节点工作流示例程序
     *
     * @return
     */
    @Override
    public boolean register() {
        WorkflowDefinition workflowDef = WorkflowDefinition.newBuilder(name, version)
                // echo任务
                .addTask(new WorkTask("echo", "echoRef")
                        .input(Pair.of("value").fromWorkflow("echoMsg")))
                // Switch节点(根据caseExpression的值进行分支)
                .addTask(new Switch("switchRef", workflow().input.get("caseExpression"))
                        // 当caseExpression的值为：'加减计算'
                        .switchCase("加减计算",
                                new WorkTask("add", "addRef")
                                        .input(Pair.of("a").fromWorkflow("a"))
                                        .input(Pair.of("b").fromWorkflow("b")),
                                new WorkTask("subtract", "subtractRef")
                                        .input(Pair.of("a").fromWorkflow("a"))
                                        .input(Pair.of("b").fromWorkflow("b")))
                        // 当caseExpression的值: '乘法计算'
                        .switchCase("乘法计算", new WorkTask("multiply", "multiplyRef")
                                .input(Pair.of("a").fromWorkflow("a"))
                                .input(Pair.of("b").fromWorkflow("b")))
                        // 其他
                        .defaultCase(new WorkTask("divide", "divideRef")
                                .input(Pair.of("a").fromWorkflow("a"))
                                .input(Pair.of("b").fromWorkflow("b")))
                ).build();
        return apiClient.getWorkflowDefClient().registerWorkflow(workflowDef, true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        List<String> list = Lists.newArrayList("加减计算", "乘法计算", "取模计算");
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("echoMsg", Map.of("value", "你好世界!"));
        reqData.put("a", 1000);
        reqData.put("b", 200);
        //随机获取一个case
        reqData.put("caseExpression", list.get(ThreadLocalRandom.current().nextInt(list.size())));
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, reqData);
    }
}
