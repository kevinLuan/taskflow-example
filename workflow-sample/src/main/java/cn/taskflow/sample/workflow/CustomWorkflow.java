package cn.taskflow.sample.workflow;

import cn.feiliu.taskflow.common.run.ExecutingWorkflow;

import java.util.concurrent.CompletableFuture;
/**
 * 自定义工作流
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
public interface CustomWorkflow {
    /**
     * 获取自定义工作流名称
     *
     * @return
     */
    String getName();

    /**
     * 获取自定义工作流版本
     *
     * @return
     */
    int getVersion();

    /**
     * 注册工作流定义
     */
    boolean register();

    /**
     * 运行工作流
     */
    CompletableFuture<ExecutingWorkflow> run();

}
