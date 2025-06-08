package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.common.dto.tasks.ExecutingTask;
import cn.feiliu.taskflow.common.dto.tasks.TaskExecResult;
import cn.feiliu.taskflow.common.enums.TaskUpdateStatus;
import cn.feiliu.taskflow.executor.task.Worker;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * @author kevin.luan
 * @since 2025-06-08
 */
@Service
public class SimpleWorker implements Worker {
    @Override
    public String getTaskDefName() {
        return "test";
    }

    @Override
    public String getTag() {
        return "测试节点";
    }

    @Override
    public String getDescription() {
        return "体验自定义节点";
    }

    @Override
    public TaskExecResult execute(ExecutingTask task) throws Throwable {
        logger.info("算子节点 {}, taskId:{}, input:{}", getTaskDefName(), task.getTaskId(), task.getInputData());
        TaskExecResult result = TaskExecResult.newTaskResult(TaskUpdateStatus.COMPLETED);
        result.addOutputData("result", "你好!!! (我来自 hello 算子节点)");
        return result;
    }

    @Override
    public Optional<String[]> getInputNames() {
        return Optional.empty();
    }

    @Override
    public Optional<String[]> getOutputNames() {
        return Optional.of(new String[]{"result"});
    }
}
