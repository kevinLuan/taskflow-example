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
    private static ApiClient getApiClient() {
        String keyId = "10000";
        String keySecret = "0cd526a9d3c34e588598f47e635c22f2";
        ApiClient apiClient = new ApiClient("http://open.taskflow.cn/api/", keyId, keySecret);
        apiClient.getTaskEngine()
                .addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        return apiClient;
    }

    private static ApiClient getLocation() {
        String keyId = "b51d09a9fb294ee5b690c4ac1b17e6a6";
        String keySecret = "8153a2e9ba3c42adb1d01db91d193533";
        ApiClient apiClient = new ApiClient("http://localhost:8081/api", keyId, keySecret);
        apiClient.getTaskEngine()
                .addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        apiClient.setUseGRPC("localhost", 9000);
        return apiClient;
    }

    public static void main(String[] args) {
        ApiClient apiClient = getApiClient();
        SimpleWorkflow workflow = new SimpleWorkflow(apiClient);
        System.out.println("注册工作流：" + workflow.register());
        String workflowId = workflow.runWorkflow();
        System.out.println("workflowId:" + workflowId);
        ExecutingWorkflow ew = Utils.waitForTerminal(workflowId, 600, apiClient);
        System.out.println(f("Workflow execution name:%s workflowId:%s, status:%s", ew.getWorkflowName(), ew.getWorkflowId(), ew.getStatus()));
    }


}