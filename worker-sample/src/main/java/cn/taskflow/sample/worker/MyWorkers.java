package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.client.spring.TaskflowWorkers;
import cn.feiliu.taskflow.sdk.workflow.task.InputParam;
import cn.feiliu.taskflow.sdk.workflow.task.WorkerTask;
import cn.taskflow.sample.pojo.DataFactory;
import cn.taskflow.sample.pojo.ExpressDelivery;
import cn.taskflow.sample.pojo.Order;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.List;

import static cn.feiliu.taskflow.common.utils.TaskflowUtils.f;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
@Slf4j
@TaskflowWorkers
public class MyWorkers {
    /**
     * 简单的echo工作任务节点
     *
     * @param value
     * @return
     */
    @WorkerTask(value = "echo", title = "echo")
    public Object echo(@InputParam("value") Object value) {
        return "echo:" + value;
    }

    @WorkerTask(value = "getOrders", title = "获取订单列表")
    public List<Order> getOrders() {
        return DataFactory.getOrders();
    }

    /**
     * 根据订单总金额智能选择快递
     *
     * @param order 订单信息
     * @return
     */
    @WorkerTask(value = "expressDelivery", title = "智能发货", threadCount = 1)
    public ExpressDelivery expressDelivery(@InputParam("order") Order order) {
        String expressType = order.getTotalAmount() >= 100 ? "sfExpress" : "emsExpress";
        log.info("智能发货 订单：{},totalAmount：{}, 指派快递：{}", order.getOrderId(),order.getTotalAmount(), expressType);
        return ExpressDelivery.builder()
                .expressType(expressType)
                .orderId(order.getOrderId())
                .name(order.getName())
                .mobile(order.getMobile())
                .address(order.getAddress())
                .build();
    }

    /**
     * 顺丰速递
     *
     * @param delivery 发货信息
     * @return
     */
    @WorkerTask(value = "sfExpress", title = "顺丰速运")
    public ExpressDelivery sfExpress(@InputParam("delivery") ExpressDelivery delivery) {
        String expressNumber = "SF-" + RandomStringUtils.randomNumeric(6);
        delivery.setExpressType("SF");
        delivery.setExpressNumber(expressNumber);
        delivery.setReason(f("您的订单：%s 已发货,顺丰快递单号为:%s。", delivery.getOrderId(), expressNumber));
        log.info("顺丰快递 :{}", delivery.getReason());
        return delivery;
    }

    /**
     * EMS 快递发货
     *
     * @param delivery 发货信息
     * @return
     */
    @WorkerTask(value = "emsExpress", title = "EMS快递", threadCount = 1)
    public ExpressDelivery emsExpress(@InputParam("delivery") ExpressDelivery delivery) {
        String expressNumber = "ems-" + RandomStringUtils.randomNumeric(6);
        delivery.setExpressType("EMS");
        delivery.setExpressNumber(expressNumber);
        delivery.setReason(f("您的订单：%s 已发货,EMS快递单号为: %s。", delivery.getOrderId(), expressNumber));
        log.info("EMS快递: {}", delivery.getReason());
        return delivery;
    }
}
