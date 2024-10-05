package cn.taskflow.sample.triggers;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.client.api.ISchedulerClient;
import cn.feiliu.taskflow.common.enums.RepeatFrequency;
import cn.feiliu.taskflow.common.enums.TriggerType;
import cn.feiliu.taskflow.open.dto.SaveScheduleRequest;
import cn.feiliu.taskflow.open.dto.WorkflowSchedule;
import cn.feiliu.taskflow.open.dto.trigger.TimerTaskTrigger;
import cn.taskflow.sample.workflows.DoWhileWorkflow;
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
public class TimerTaskTriggerService {
    @Autowired
    private ApiClient apiClient;
    @Autowired
    private DoWhileWorkflow doWhileWorkflow;

    @PostConstruct
    public void createTimerSchedule() {
        SaveScheduleRequest request = new SaveScheduleRequest();
        request.setTriggerType(TriggerType.SCHEDULE);
        request.setName("timerTaskTrigger");
        request.setDescription("测试TimerTask调度触发器,每小时重复");
        request.setTimerTaskTrigger(TimerTaskTrigger.newSimpleType(RepeatFrequency.HOURLY));
        request.setStartTime(System.currentTimeMillis());
        request.setEndTime(DateUtils.addHours(new Date(), 5).getTime());
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

