/*
 *  Copyright (c) 2025 Taskflow, Inc.
 *  All rights reserved.
 */
package cn.taskflow.sample.pay;

import lombok.Data;

/**
 * 转账风控请求
 *
 * @author kevin.luan
 * @since 2025-05-29
 */
@Data
public class RiskControlResult {
    /*交易流水号*/
    private String tradeId;
    /*风险级别*/
    private RiskLevel riskLevel;
    /*原因*/
    private String reason;

    public RiskControlResult(String tradeId, RiskLevel riskLevel, String reason) {
        this.tradeId = tradeId;
        this.riskLevel = riskLevel;
        this.reason = reason;
    }

    public RiskControlResult() {

    }

    public enum RiskLevel {
        /*正常请求*/
        NORMAL,
        /*低风险*/
        LOW_RISK,
        /*高风险*/
        HIGH_RISK
    }
}
