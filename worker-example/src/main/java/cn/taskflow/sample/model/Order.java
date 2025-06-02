package cn.taskflow.sample.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
@Data
public class Order {
    // 订单ID
    private String orderId;
    // 用户ID
    private String userId;
    // 下单日期
    private Date orderDate;
    // 总金额
    private double totalAmount;
    // 订单状态
    private String status;
    // 订单项列表
    private List<OrderItem> items = new ArrayList<>();
    /*联系人姓名*/
    private String name;
    /*联系人手机号*/
    private String mobile;
    /*地址*/
    private String address;
}
