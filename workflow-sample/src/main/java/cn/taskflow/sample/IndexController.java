package cn.taskflow.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @PostConstruct
    public void init() {
        log.info("Welcome to the Workflow Sample service:  http://localhost:8200/index");
    }

    @RequestMapping(value = {"/", "/index"})
    public Object index() {
        return "Hello Workflow";
    }
}
