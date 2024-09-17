package cn.taskflow.sample.utils;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import java.util.concurrent.TimeoutException;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-17
 */
public class Utils {
    public static ExecutingWorkflow waitForTerminal(String workflowId, int waitForSeconds, ApiClient apiClient) {
        try {
            long startTime = System.currentTimeMillis();
            for (; ; ) {
                ExecutingWorkflow workflow = apiClient.getWorkflowClient().getWorkflow(workflowId, true);
                if (workflow.getStatus().isTerminal()) {
                    return workflow;
                } else {
                    long cost = System.currentTimeMillis() - startTime;
                    if (cost >= waitForSeconds * 1000) {
                        throw new TimeoutException("Timeout exceeded while waiting for workflow to reach terminal state.");
                    }
                    long remaining = waitForSeconds * 1000 - cost;
                    Thread.sleep(Math.min(100, remaining));
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
