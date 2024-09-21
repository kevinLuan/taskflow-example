package cn.taskflow.sample;

import cn.feiliu.taskflow.client.ApiClient;
import cn.feiliu.taskflow.common.metadata.tasks.TaskDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
@RestController
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private ApiClient apiClient;

    @PostConstruct
    public void init() {
        log.info("Welcome to the Worker Sample service:  http://localhost:8100/index");
    }

    /**
     * 获取所有的任务列表
     *
     * @return
     */
    @RequestMapping(value = {"/", "/index"})
    public List<TaskDefinition> index() {
        return apiClient.getTaskEngine().getTaskDefs();
    }
}
