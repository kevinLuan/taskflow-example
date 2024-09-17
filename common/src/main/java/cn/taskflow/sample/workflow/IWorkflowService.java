package cn.taskflow.sample.workflow;

/**
 * 自定义任务流服务
 *
 * @author SHOUSHEN.LUAN
 * @since 2024-09-03
 */
public interface IWorkflowService {
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
    String runWorkflow();

}
