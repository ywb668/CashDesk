package entity;

import lombok.Data;

@Data
public class Goods {
    private Integer id;
    private String name;
    private String introduce;
    private Integer stock;
    private String unit; //单位
    private Integer price; //存入数据库为整数
    private Integer discount;

    private Integer buyGoodsNum;

    public double getPrice() {
        return price * 1.0 / 100;
    }

    public Integer getPriceInt() {
        return price;
    }
}
