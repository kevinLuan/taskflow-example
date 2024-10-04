package cn.taskflow.sample.triggers;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.api.ISchedulerClient;
import cn.feiliu.taskflow.common.enums.TriggerType;
import cn.feiliu.taskflow.open.dto.SaveScheduleRequest;
import cn.feiliu.taskflow.open.dto.WorkflowSchedule;
import cn.taskflow.sample.workflows.DoWhileWorkflow;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-04
 */
@Service
public class WebhookTrigger {
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private DoWhileWorkflow doWhileWorkflow;

    @PostConstruct
    public void init() {
        WorkflowSchedule webhookSchedule = createWebhookSchedule();
        triggerWebhook(webhookSchedule);
    }

    public WorkflowSchedule createWebhookSchedule() {
        SaveScheduleRequest request = new SaveScheduleRequest();
        request.setTriggerType(TriggerType.WEBHOOK);
        request.setName("doWhileWebhookTrigger");
        request.setDescription("测试Webhook调度触发器");
        request.setStartTime(System.currentTimeMillis());
        request.setEndTime(DateUtils.addMinutes(new Date(), 5).getTime());
        request.setStartWorkflowRequest(doWhileWorkflow.getRequest());
        request.setOverwrite(true);
        ISchedulerClient schedulerClient = apiClient.getApis().getSchedulerClient();
        schedulerClient.saveSchedule(request);
        return schedulerClient.getSchedule(request.getName());
    }

    public void triggerWebhook(WorkflowSchedule workflowSchedule) {
        HttpClient client = HttpClient.newHttpClient();
        String url = apiClient.getBasePath() + "/trigger/webhook/" + workflowSchedule.getWebhookTrigger().getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("x-authorization", apiClient.getToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status code: " + response.statusCode());
            System.out.println("Response body: " + response.body());
        } catch (Exception e) {
            System.err.println("Error triggering webhook: " + e.getMessage());
        }
    }
}
