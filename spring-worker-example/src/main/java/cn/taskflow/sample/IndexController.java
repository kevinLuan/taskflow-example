package cn.taskflow.sample;

import cn.feiliu.common.api.model.resp.DataResult;
import cn.feiliu.taskflow.client.ApiClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
@RestController
public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    private ApiClient apiClient;

    @RequestMapping({"/index", "/"})
    public DataResult<Object> index() {
        return DataResult.ok("Hello world");
    }
}
