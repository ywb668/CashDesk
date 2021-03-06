package servlet;

import commons.OrderStatus;
import entity.Account;
import entity.Goods;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/pay")
public class ReadyBuyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String goodsAndNum = req.getParameter("goodsIDAndNum");

        System.out.println("goodsAndNum:" + goodsAndNum);

        String[] strings = goodsAndNum.split(",");


        HttpSession session = req.getSession();
        Account account = (Account) session.getAttribute("user");

        //生成订单
        Order order = new Order();
        //设置订单号id，用户id，用户姓名，生成时间，订单金额，实际金额属性
        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        System.out.println(account.getUserName());
        order.setAccount_name(account.getUserName());

        List<Goods> list = new ArrayList<>();
        for (String s : strings) {
            String[] str = s.split("-");
            //根据id查询商品是否存在
            Goods goods = getGoods(Integer.parseInt(str[0]));


            if (goods != null) {
                list.add(goods);
                goods.setBuyGoodsNum(Integer.parseInt(str[1]));
            } else {
                throw new RuntimeException("该商品不存在");
            }
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setCreate_time(LocalDateTime.now().format(formatter));

        int totalMoney = 0;
        int actualMoney = 0;
        for (Goods goods1 : list) {
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setGoodsId(goods1.getId());
            item.setGoodsName(goods1.getName());
            item.setGoodsIntroduce(goods1.getIntroduce());
            item.setGoodsNum(goods1.getBuyGoodsNum());
            item.setGoodsUnit(goods1.getUnit());
            item.setGoodsPrice(goods1.getPriceInt());
            item.setGoodsDiscount(goods1.getDiscount());

            order.orderItems.add(item);

            totalMoney += goods1.getPriceInt() * goods1.getBuyGoodsNum();
            actualMoney += goods1.getPriceInt() * goods1.getBuyGoodsNum() * goods1.getDiscount() / 100;
        }

        System.out.println(order.orderItems);

        order.setTotal_money(totalMoney);
        order.setActual_amount(actualMoney);
        order.setOrder_status(OrderStatus.PAYING);


        HttpSession session1 = req.getSession();
        session1.setAttribute("order", order);
        session1.setAttribute("goodsList", list);

        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Title</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<p>【用户名称】: " + order.getAccount_name() + "</p>\n" +
                "<p>【订单编号】: " + order.getId() + "</p>\n" +
                "<p>【订单状态】: " + order.getOrder_statusName().getDesc() + "</p>\n" +
                "<p>【创建时间】: " + order.getCreate_time() + "</p>\n" +
                "<p>编号 名称 数量 单位 单价(元)</p>\n" +
                "<ol>\n");
        for (OrderItem orderItem : order.orderItems) {
            sb.append("<li>" + " " + orderItem.getGoodsName() + " " +
                    orderItem.getGoodsNum() + " " + orderItem.getGoodsUnit() + " " +
                    orderItem.getGoodsPrice() + "</li>\n");
        }
        sb.append("</ol>\n" +
                "<p>【总金额】:  " + order.getTotal_money() + "</p>\n" +
                "<p>【优惠金额】: " + order.getDiscount() + "</p>\n" +
                "<p>【实际金额】: " + order.getActual_amount() + "</p>\n" +
                "<a href=\"buyGoodsServlet\">支付</a>\n" +
                "<a href=\"index.html\">取消</a>\n" +
                "</body>\n" +
                "</html>");
        System.out.println(sb.toString());
        PrintWriter writer = resp.getWriter();
        writer.write(sb.toString());
        writer.close();
    }

    private Goods getGoods(Integer id) {
        String sql = "select id,name,introduce,stock,unit,price,discount from goods where id=?";

        Goods goods = null;
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    goods = new Goods();
                    goods.setId(resultSet.getInt("id"));
                    goods.setName(resultSet.getString("name"));
                    goods.setIntroduce(resultSet.getString("introduce"));
                    goods.setStock(resultSet.getInt("stock"));
                    goods.setUnit(resultSet.getString("unit"));
                    goods.setPrice(resultSet.getInt("price"));
                    goods.setDiscount(resultSet.getInt("discount"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return goods;
    }
}
