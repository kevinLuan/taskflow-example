package cn.taskflow.sample.triggers;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.api.ISchedulerClient;
import cn.feiliu.taskflow.common.enums.TriggerType;
import cn.feiliu.taskflow.open.dto.SaveScheduleRequest;
import cn.feiliu.taskflow.open.dto.WorkflowSchedule;
import cn.feiliu.taskflow.open.dto.trigger.CronTrigger;
import cn.taskflow.sample.workflows.DoWhileWorkflow;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Date;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-10-04
 */
@Service
public class CronTriggerService {
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private DoWhileWorkflow doWhileWorkflow;
    @PostConstruct
    public void createCronSchedule() {
        SaveScheduleRequest request = new SaveScheduleRequest();
        request.setTriggerType(TriggerType.CRON);
        request.setName("doWhileCronTrigger");
        request.setDescription("测试Cron调度触发器,每分钟执行一次");
        //cron 表达式: 每分钟执行一次
        request.setCronTrigger(new CronTrigger("0 * * * * *"));
        request.setStartTime(System.currentTimeMillis());
        request.setEndTime(DateUtils.addMinutes(new Date(), 5).getTime());
        request.setStartWorkflowRequest(doWhileWorkflow.getRequest());
        request.setOverwrite(true);
        ISchedulerClient schedulerClient = apiClient.getApis().getSchedulerClient();
        WorkflowSchedule workflowSchedule = schedulerClient.getSchedule(request.getName());
        if (workflowSchedule == null) {
            schedulerClient.saveSchedule(request);
        } else {
            try {
                schedulerClient.pauseSchedule(request.getName());
                schedulerClient.saveSchedule(request);
            } finally {
                schedulerClient.resumeSchedule(request.getName());
            }
        }
    }
}
