package cn.taskflow.sample.worker;

import cn.feiliu.common.api.utils.MapBuilder;
import cn.feiliu.taskflow.annotations.InputParam;
import cn.feiliu.taskflow.annotations.WorkerTask;
import cn.feiliu.taskflow.executor.task.TaskContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author kevin.luan
 * @since 2025-06-08
 */
@Service
public class FirstWorker {
    static Logger logger = LoggerFactory.getLogger(FirstWorker.class);

    @WorkerTask(value = "hello_world", tag = "你好世界", description = "第一个 hello_world 算子节点", open = false)
    public Map<String, Object> hello_world(@InputParam("msg") String msg) {
        logger.info("算子节点 {}, taskId:{}, input:{}", TaskContext.get().getTask().getTaskDefName(), TaskContext.get().getTaskId(), msg);
        return MapBuilder.newBuilder().put("result", msg).build();
    }
}
