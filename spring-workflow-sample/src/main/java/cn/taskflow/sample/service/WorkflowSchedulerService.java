package cn.taskflow.sample.service;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.open.dto.SaveScheduleRequest;
import cn.feiliu.taskflow.open.dto.WorkflowSchedule;
import cn.taskflow.sample.workflow.DoWhileWorkflow;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * 工作流调度
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-21
 */
@Service
public class WorkflowSchedulerService {
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private DoWhileWorkflow doWhileWorkflow;

    @PostConstruct
    public void createSchedule() {
        SaveScheduleRequest request = new SaveScheduleRequest();
        request.setName("doWhileWorkflowSchedule");
        //cron 表达式: 每分钟执行一次
        request.setCronExpression("0 * * * * *");
        request.setScheduleStartTime(new Date());
        request.setScheduleEndTime(DateUtils.addMinutes(new Date(), 5));
        request.setStartWorkflowRequest(doWhileWorkflow.getRequest());
        request.setCreatedBy("your_name");//
        request.setUpdatedBy("your_name");
        request.setOverwrite(true);
        WorkflowSchedule workflowSchedule = apiClient.getSchedulerClient().getSchedule(request.getName());
        if (workflowSchedule == null) {
            apiClient.getSchedulerClient().saveSchedule(request);
        } else {
            try {
                apiClient.getSchedulerClient().pauseSchedule(request.getName());
                apiClient.getSchedulerClient().saveSchedule(request);
            } finally {
                apiClient.getSchedulerClient().resumeSchedule(request.getName());
            }
        }
    }
}

