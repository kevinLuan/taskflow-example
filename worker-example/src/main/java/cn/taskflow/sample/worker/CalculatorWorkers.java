package cn.taskflow.sample.worker;

import cn.feiliu.taskflow.annotations.InputParam;
import cn.feiliu.taskflow.annotations.OutputParam;
import cn.feiliu.taskflow.annotations.WorkerTask;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-01
 */
public class CalculatorWorkers {
    /**
     * 加法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相加的结果
     */
    @OutputParam("result")
    @WorkerTask(value = "add", tag = "加法计算", description = "演示简单的加法计算 ( a + b = ? )")
    public int add(@InputParam("a") int a, @InputParam("b") int b) {
        System.out.println("add( " + a + " + " + b + " )");
        return a + b;
    }

    /**
     * 减法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相减的结果
     */
    @WorkerTask(value = "subtract", tag = "减法计算", description = "演示简单的减法计算 ( a - b = ? )")
    public int subtract(@InputParam("a") int a, @InputParam("b") int b) {
        System.out.println("subtract( " + a + " - " + b + " )");
        return a - b;
    }

    /**
     * 乘法方法
     *
     * @param a 第一个整数
     * @param b 第二个整数
     * @return 两个整数相乘的结果
     */
    @WorkerTask(value = "multiply", tag = "乘法计算", description = "演示简单的乘法计算 ( a x b = ? )")
    public int multiply(@InputParam("a") int a, @InputParam("b") int b) {
        System.out.println("multiply( " + a + " x " + b + " )");
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
    @WorkerTask(value = "divide", tag = "除法计算", description = "演示简单的除法计算 ( a ÷ b = ? )")
    public double divide(@InputParam("a") int a, @InputParam("b") int b) throws ArithmeticException {
        System.out.println("divide( " + a + " / " + b + " )");
        if (b == 0) {
            throw new ArithmeticException("除数不能为0");
        }
        return (double) a / b;
    }
}
