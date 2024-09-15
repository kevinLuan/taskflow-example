package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.client.spring.TaskflowWorkers;
import cn.feiliu.taskflow.sdk.workflow.task.InputParam;
import cn.feiliu.taskflow.sdk.workflow.task.OutputParam;
import cn.feiliu.taskflow.sdk.workflow.task.WorkerTask;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
@TaskflowWorkers
public class CalculatorWorkers {
    /**
     * 加法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相加的结果
     */
    @OutputParam("sum")
    @WorkerTask(value="add",title = "加法")
    public int add(@InputParam("a") int a,@InputParam("b") int b) {
        return a + b;
    }

    /**
     * 减法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相减的结果
     */
    @WorkerTask(value="subtract",title = "减法")
    public int subtract(@InputParam("a") int a,@InputParam("b") int b) {
        return a - b;
    }

    /**
     * 乘法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相乘的结果
     */
    @WorkerTask(value="multiply",title = "乘法")
    public int multiply(@InputParam("a") int a,@InputParam("b") int b) {
        return a * b;
    }

    /**
     * 除法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相除的结果
     * @throws ArithmeticException 如果第二个整数为0
     */
    @WorkerTask(value="divide",title = "除法")
    public double divide(@InputParam("a") int a,@InputParam("b") int b) throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("除数不能为0");
        }
        return (double) a / b;
    }
}
