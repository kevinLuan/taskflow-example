package cn.taskflow.sample.pojo;

import lombok.Data;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
@Data
public class OrderItem {
    private String productId; // 商品ID
    private int quantity; // 数量
    private double price; // 价格
}
