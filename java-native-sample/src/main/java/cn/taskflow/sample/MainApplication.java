package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;
import cn.taskflow.sample.utils.PropertiesReader;
import cn.taskflow.sample.utils.Utils;
import cn.taskflow.sample.worker.CalculatorWorkers;
import cn.taskflow.sample.worker.MyWorkers;
import cn.taskflow.sample.workflow.SimpleWorkflow;

import java.io.IOException;

import static cn.feiliu.taskflow.common.utils.TaskflowUtils.f;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-17
 */
public class MainApplication {
    private static ApiClient getLocation() throws IOException {
        PropertiesReader reader = new PropertiesReader("taskflow_config.properties");
        String url = reader.getProperty("taskflow.server.url", "http://localhost:8082/api");
        String keyId = reader.getProperty("taskflow.security.client.key-id", "19242c73065");
        String keySecret = reader.getProperty("taskflow.security.client.secret", "8153a2e9ba3c42adb1d01db91d193533");
        String grpcHost = reader.getProperty("taskflow.grpc.host");
        Integer grpcPort = reader.getInt("taskflow.grpc.port");
        ApiClient apiClient = new ApiClient(url, keyId, keySecret);
        apiClient.getApis().getTaskEngine().addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        if (grpcHost != null && grpcPort != null) {
            apiClient.setUseGRPC(grpcHost, grpcPort);
            Boolean grpcSsl = reader.getBoolean("taskflow.grpc.ssl");
            if (grpcSsl != null) {
                apiClient.setUseSSL(grpcSsl);
            }
        }
        return apiClient;
    }

    public static void main(String[] args) throws IOException {
        ApiClient apiClient = getLocation();
        SimpleWorkflow workflow = new SimpleWorkflow(apiClient);
        System.out.println("注册工作流：" + workflow.register());
        String workflowId = workflow.runWorkflow();
        System.out.println("workflowId:" + workflowId);
        ExecutingWorkflow ew = Utils.waitForTerminal(workflowId, 600, apiClient);
        System.out.println(f("Workflow execution name:%s workflowId:%s, status:%s", ew.getWorkflowName(), ew.getWorkflowId(), ew.getStatus()));
    }


}