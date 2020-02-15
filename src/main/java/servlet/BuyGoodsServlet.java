package servlet;

import commons.OrderStatus;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyGoodsServlet")
public class BuyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        HttpSession session = req.getSession();
        Order order = (Order) session.getAttribute("order");
        List<Goods> list = (List<Goods>) session.getAttribute("goodsList");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setFinish_time(LocalDateTime.now().format(formatter));
        order.setOrder_status(OrderStatus.OK);

        //提交订单
        boolean effect = this.commitOrder(order);

        if (effect) {
            //成功
            //遍历所有的货物，把这些货物的库存进行修改
            for (Goods goods : list) {
                boolean isUpdate = updataAfterPay(goods, goods.getBuyGoodsNum());
                if (isUpdate) {
                    System.out.println("更新库存成功");
                } else {
                    System.out.println("更新库存失败");
                }
            }
            resp.sendRedirect("buyGoodsSuccess.html");
        } else {
            //失败
        }
    }

    private boolean updataAfterPay(Goods goods, Integer buyGoodsNum) {
        String sql = "update goods set stock=? where id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goods.getStock() - buyGoodsNum);
            statement.setInt(2, goods.getId());
            if(statement.executeUpdate() == 1)
                return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean commitOrder(Order order) {
        String insertOrder = "insert into `order`(id,account_id,account_name,create_time,finish_time,actual_amount,total_money,order_status) " +
                "VALUES (?,?,?,?,?,?,?,?)";
        String insertOrderItem = "insert into order_item(order_id,goods_id,goods_name,goods_introduce,goods_num,goods_unit,goods_price,goods_discount) " +
                "VALUE(?,?,?,?,?,?,?,?)";
        Connection connection = null;
        try {
            connection = DBUtil.getConnection(false);
            try (PreparedStatement statement1 = connection.prepareStatement(insertOrder);
                 PreparedStatement statement2 = connection.prepareStatement(insertOrderItem)) {

                statement1.setString(1, order.getId());
                statement1.setInt(2, order.getAccount_id());
                statement1.setString(3, order.getAccount_name());
                statement1.setString(4, order.getCreate_time());
                statement1.setString(5, order.getFinish_time());
                statement1.setString(6, String.valueOf(order.getActual_amountInt()));
                statement1.setString(7, String.valueOf(order.getTotal_moneyInt()));
                statement1.setString(8, String.valueOf(order.getOrder_statusName().getFlg()));
                if (statement1.executeUpdate() == 0)
                    throw new RuntimeException("订单插入失败");


                for (OrderItem orderItem : order.orderItems) {
                    statement2.setString(1, orderItem.getOrderId());
                    statement2.setInt(2, orderItem.getGoodsId());
                    statement2.setString(3, orderItem.getGoodsName());
                    statement2.setString(4, orderItem.getGoodsIntroduce());
                    statement2.setInt(5, orderItem.getGoodsNum());
                    statement2.setString(6, orderItem.getGoodsUnit());
                    statement2.setInt(7, orderItem.getGoodsPriceInt());
                    statement2.setInt(8, orderItem.getGoodsDiscount());
                    //将每一项statement 进行缓存
                    statement2.addBatch();
                }

                //将缓存的所有statement执行，将返回的结果存储在数组中
                int[] ints = statement2.executeBatch();

                for (int i : ints) {
                    if (i == 0)
                        throw new RuntimeException("订单项插入失败");
                }

                //如果订单和订单项都保存成功才提交
                connection.commit();
                return true;
            } catch (RuntimeException e) {
                e.printStackTrace();
                //事务的回滚
                connection.rollback();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
