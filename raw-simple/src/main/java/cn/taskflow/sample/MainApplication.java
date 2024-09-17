package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.taskflow.sample.utils.Utils;
import cn.taskflow.sample.worker.CalculatorWorkers;
import cn.taskflow.sample.worker.MyWorkers;
import cn.taskflow.sample.workflow.SimpleWorkflow;

import java.util.concurrent.TimeoutException;

import static cn.feiliu.taskflow.common.utils.TaskflowUtils.f;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-17
 */
public class MainApplication {
    static String HOST = "http://localhost:8082/api";
    static String KEY_ID = "b51d09a9fb294ee5b690c4ac1b17e6a6";
    static String SECRET = "8153a2e9ba3c42adb1d01db91d193533";
    static String GRPC_HOST = "localhost";
    static int GRPC_PORT = 9000;

    public static void main(String[] args) {
        ApiClient apiClient = new ApiClient(HOST, KEY_ID, SECRET);
        apiClient.getTaskEngine()
                .addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        apiClient.setUseGRPC(GRPC_HOST, GRPC_PORT);
        SimpleWorkflow workflow = new SimpleWorkflow(apiClient);
        System.out.println("注册工作流：" + workflow.register());
        String workflowId = workflow.runWorkflow();
        System.out.println("workflowId:" + workflowId);
        ExecutingWorkflow ew = Utils.waitForTerminal(workflowId, 60, apiClient);
        System.out.println(f("Workflow execution name:%s workflowId:%s, status:%s", ew.getWorkflowName(), ew.getWorkflowId(), ew.getStatus()));
    }


}