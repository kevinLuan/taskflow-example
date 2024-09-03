package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import java.util.concurrent.CompletableFuture;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
public interface IWorkflow {
    String getName();

    int getVersion();

    /**
     * 创建工作流定义
     */
    boolean createWorkflow();

    /**
     * 运行工作流
     */
    CompletableFuture<ExecutingWorkflow> run();

}
