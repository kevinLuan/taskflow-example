package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.taskflow.sample.utils.PropertiesReader;
import cn.taskflow.sample.worker.CalculatorWorkers;
import cn.taskflow.sample.worker.MyWorkers;

import java.io.IOException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-17
 */
public class MainApplication {
    private static ApiClient getLocation() throws IOException {
        PropertiesReader reader = new PropertiesReader("taskflow_config.properties");
        String url = reader.getProperty("taskflow.server.url");
        String keyId = reader.getProperty("taskflow.security.client.key-id");
        String keySecret = reader.getProperty("taskflow.security.client.secret");
        ApiClient apiClient = new ApiClient(url, keyId, keySecret);
        apiClient.getApis().getTaskEngine().addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        return apiClient;
    }

    public static void main(String[] args) throws IOException {
        ApiClient apiClient = getLocation();
    }


}