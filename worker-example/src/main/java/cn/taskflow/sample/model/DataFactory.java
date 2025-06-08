package cn.taskflow.sample.model;

import cn.feiliu.common.api.encoder.EncoderFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author SHOUSHEN.LUAN
 * @since 2024-09-07
 */
public class DataFactory {
    static AtomicLong idGenerator = new AtomicLong(0);
    static String json = ("[" +
            "    {" +
            "        'orderId': 'ORD1234'," +
            "        'userId': 'USER001'," +
            "        'orderDate': 1696125600000," +
            "        'totalAmount': 80," +
            "        'status': 'PENDING'," +
            "        'items': [" +
            "            {" +
            "                'productId': 'PROD1001'," +
            "                'quantity': 1," +
            "                'price': 50" +
            "            }," +
            "            {" +
            "                'productId': 'PROD1002'," +
            "                'quantity': 1," +
            "                'price': 30" +
            "            }" +
            "        ]" +
            "    }," +
            "    {" +
            "        'orderId': 'ORD456'," +
            "        'userId': 'USER001'," +
            "        'orderDate': 1696125600000," +
            "        'totalAmount': 250," +
            "        'status': 'PENDING'," +
            "        'items': [" +
            "            {" +
            "                'productId': 'PROD1001'," +
            "                'quantity': 2," +
            "                'price': 100" +
            "            }," +
            "            {" +
            "                'productId': 'PROD1002'," +
            "                'quantity': 1," +
            "                'price': 50" +
            "            }" +
            "        ]" +
            "    }" +
            "]").replace('\'','"');

    public static List<Order> getOrders() {
        List<Order>orders = EncoderFactory.getJsonEncoder().decodeList(json, Order.class);
        for (Order order : orders) {
            order.setOrderId("ORDER_"+idGenerator.incrementAndGet());
        }
        return orders;
    }
}
