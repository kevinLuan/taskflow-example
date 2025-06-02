package cn.taskflow.sample.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpressDelivery {
    /*订单号*/
    private String orderId;
    /*快递类型*/
    private String expressType;
    /*快递单号*/
    private String expressNumber;
    private String name;
    private String mobile;
    private String address;
    private String reason;
}
