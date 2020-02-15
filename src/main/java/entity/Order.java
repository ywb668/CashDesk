package entity;

import commons.OrderStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_money;
    private OrderStatus order_status;

    public String getOrder_status() {
        return order_status.getDesc();
    }

    public OrderStatus getOrder_statusName() {
        return order_status;
    }

    //订单项的内容
    public List<OrderItem> orderItems = new ArrayList<>();

    public double getActual_amount() {
        return actual_amount * 1.0 / 100;
    }

    public Integer getActual_amountInt() {
        return actual_amount;
    }

    public double getTotal_money() {
        return total_money * 1.0 / 100;
    }

    public Integer getTotal_moneyInt() {
        return total_money;
    }

    //获取优惠金额
    public double getDiscount() {
        return (this.getTotal_moneyInt() - this.getActual_amountInt()) * 1.0 / 100;
    }
}
