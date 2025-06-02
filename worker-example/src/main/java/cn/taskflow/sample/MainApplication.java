package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.taskflow.sample.utils.PropertiesReader;
import cn.taskflow.sample.worker.CalculatorWorkers;
import cn.taskflow.sample.worker.MyWorkers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-17
 */
public class MainApplication {
    private static final Logger logger = LoggerFactory.getLogger(MainApplication.class);

    private static ApiClient getLocation() throws IOException {
        logger.info("Initializing ApiClient...");
        PropertiesReader reader = new PropertiesReader("taskflow_config.properties");
        String url = reader.getProperty("taskflow.base-url");
        String keyId = reader.getProperty("taskflow.client.key-id");
        String keySecret = reader.getProperty("taskflow.client.secret");
        logger.info("Connecting to TaskFlow server at: {}", url);
        ApiClient apiClient = new ApiClient(url, keyId, keySecret);
        apiClient.getApis().getTaskEngine().addWorkers(
                        new MyWorkers(),
                        new CalculatorWorkers()
                ).initWorkerTasks()
                .startRunningTasks();
        logger.info("ApiClient initialized successfully");
        return apiClient;
    }

    public static void main(String[] args) throws IOException {
        logger.info("Starting TaskFlow worker application...");
        ApiClient apiClient = getLocation();
        logger.info("TaskFlow worker application started successfully");
    }
}