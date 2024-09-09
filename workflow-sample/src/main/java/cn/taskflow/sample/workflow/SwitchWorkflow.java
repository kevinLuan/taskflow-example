package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.core.FeiLiuWorkflow;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
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
public class SwitchWorkflow implements CustomWorkflow {
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
        FeiLiuWorkflow<Map<String, Object>> workflow = apiClient.newWorkflowBuilder(name, version)
                .add(new WorkTask("echo", "echoRef")
                        .input("value", "${workflow.input.echoMsg}"))
                .add(new Switch("switchRef", "${workflow.input.caseExpression}")
                        .switchCase("AddOrSubtract",
                                new WorkTask("add", "addRef")
                                        .input("a", "${workflow.input.a}")
                                        .input("b", "${workflow.input.b}"),
                                new WorkTask("subtract", "subtractRef")
                                        .input("a", "${workflow.input.a}")
                                        .input("b", "${workflow.input.b}"))
                        .switchCase("Multiply", new WorkTask("multiply", "multiplyRef")
                                .input("a", "${workflow.input.a}")
                                .input("b", "${workflow.input.b}"))
                        .defaultCase(new WorkTask("divide", "divideRef")
                                .input("a", "${workflow.input.a}")
                                .input("b", "${workflow.input.b}"))
                ).build();
        return workflow.registerWorkflow(true,true);
    }

    @Override
    public CompletableFuture<ExecutingWorkflow> run() {
        List<String> list = Lists.newArrayList("AddOrSubtract", "Multiply", "Other");
        Map<String, Object> reqData = new HashMap<>();
        reqData.put("echoMsg", Map.of("value", "你好世界!"));
        reqData.put("a", 1000);
        reqData.put("b", 200);
        //随机获取一个case
        reqData.put("caseExpression", list.get(ThreadLocalRandom.current().nextInt(list.size())));
        return apiClient.getWorkflowExecutor().executeWorkflow(name, version, reqData);
    }
}
