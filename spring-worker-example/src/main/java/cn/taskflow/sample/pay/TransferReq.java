package cn.taskflow.sample.pay;

import lombok.Data;

/**
 * 转账请求
 *
 * @author kevin.luan
 * @since 2025-06-21
 */
@Data
public class TransferReq {
    /*转义交易流水号*/
    private String tradeId;
    private String fromAccount;
    private String toAccount;
    private Integer amount;
}
