package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.utils.PropertiesReader;
import cn.feiliu.taskflow.utils.TaskflowConfig;
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
        PropertiesReader reader = new PropertiesReader("taskflow_config.properties");
        TaskflowConfig config = reader.toConfig();
        ApiClient apiClient = new ApiClient(config);
        apiClient.addWorker(
                new MyWorkers(),
                new CalculatorWorkers()
        ).start();
        return apiClient;
    }

    public static void main(String[] args) throws IOException {
        ApiClient apiClient = getLocation();
        logger.info("TaskFlow worker application started successfully");
    }
}