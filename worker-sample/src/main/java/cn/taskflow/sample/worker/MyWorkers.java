package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.client.spring.SpringWorkers;
import cn.feiliu.taskflow.sdk.workflow.task.InputParam;
import cn.feiliu.taskflow.sdk.workflow.task.WorkerTask;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
@SpringWorkers
public class MyWorkers {
    @WorkerTask(value = "echo",title = "echo")
    public Object echo(@InputParam("value") Object value){
        return "echo:"+value;
    }
}
