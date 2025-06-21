package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.annotations.InputParam;
import cn.feiliu.taskflow.annotations.WorkerTask;
import cn.feiliu.taskflow.executor.task.NonRetryableException;
import cn.taskflow.sample.pay.RiskControlResult;
import cn.taskflow.sample.pay.TransferReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * 转账处理流程
 *
 * @author kevin.luan
 * @since 2025-06-21
 */
@Service
public class TransferProcessService {
    static Logger log = LoggerFactory.getLogger(TransferProcessService.class);

    /**
     * 转账风控处理节点
     *
     * @return
     */
    @WorkerTask(value = "transferRiskControl", tag = "转账风控", description = "验证转账风控")
    public RiskControlResult transferRiskControl(@InputParam("req") TransferReq req) {
        if (req.getAmount() != null && req.getAmount() > 0) {
            if (req.getAmount() <= 200) {
                return new RiskControlResult(req.getTradeId(), RiskControlResult.RiskLevel.NORMAL, "小额转账，不做拦截");
            } else if (req.getAmount() <= 2000) {
                return new RiskControlResult(req.getTradeId(), RiskControlResult.RiskLevel.LOW_RISK, "大额转账延迟一天到账");
            } else {
                return new RiskControlResult(req.getTradeId(), RiskControlResult.RiskLevel.HIGH_RISK, "超过最大限额，转账金额：" + req.getAmount());
            }
        } else {
            throw new NonRetryableException("转账金额不能为空");
        }
    }

    /**
     * 执行转账处理
     */
    @WorkerTask(value = "doTransfer", tag = "执行转账", description = "演示模拟转账流程")
    public boolean doTransfer(@InputParam("req") TransferReq req) {
        log.info("转账处理，{} 给 {} 转账 {} 元", req.getFromAccount(), req.getToAccount(), req.getAmount());
        return true;
    }

    /**
     * 拒绝转账处理
     */
    @WorkerTask(value = "rejectTransfer", tag = "拒绝转账",description = "演示拒绝转账处理")
    public void rejectTransfer(@InputParam("req") TransferReq req, @InputParam("riskControl") RiskControlResult riskControl) {
        log.info("终止转账, {} 给 {} 转账 {} 元, 原因: {}", req.getFromAccount(), req.getToAccount(), req.getAmount(), riskControl.getReason());
    }
}
