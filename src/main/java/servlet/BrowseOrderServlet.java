package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import commons.OrderStatus;
import entity.Account;
import entity.Order;
import entity.OrderItem;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("browseOrder");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        HttpSession session = req.getSession();
        Account account = (Account)session.getAttribute("user");

        //1.根据当前用户的id进行订单的查找
        Integer accountId = account.getId();
        List<Order> orders = getOrder(accountId);

        //2.查询结果可能是多个订单，返回一个List<Order>
        if(orders.size() == 0) {
            //3.结果为空说明没有订单
            System.out.println("订单链表为空");
        } else {
            //4.不为空，将list转换为接送对象发送给前端
            ObjectMapper mapper = new ObjectMapper();

            PrintWriter pw = resp.getWriter();
            mapper.writeValue(pw, orders);

            PrintWriter writer = resp.getWriter();
            writer.write(pw.toString());
            writer.close();
        }
    }

    private List<Order> getOrder(Integer accountId) {
        List<Order> list = new ArrayList<>();

        String fileName = "query_order_by_account.sql";
        String sql = getSql(fileName);

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, accountId);
            try (ResultSet resultSet = statement.executeQuery()) {

                Order order = null;
                while(resultSet.next()) {
                    //订单解析
                    if(order == null) {
                        order = new Order();
                        parseOrder(order, resultSet);
                        list.add(order);
                    }

                    String orderId = resultSet.getString("order_id");

                    if(!order.getId().equals(orderId)) {
                        order = new Order();
                        parseOrder(order, resultSet);
                        list.add(order);
                    }

                    //订单项解析
                    OrderItem orderItem = new OrderItem();
                    parseOrderItem(orderItem, resultSet);
                    order.orderItems.add(orderItem);

                }
                System.out.println(list);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    private void parseOrderItem(OrderItem orderItem, ResultSet resultSet) throws SQLException {
        orderItem.setOrderId(resultSet.getString("order_id"));
        orderItem.setGoodsId(resultSet.getInt("goods_id"));
        orderItem.setGoodsName(resultSet.getString("goods_name"));
        orderItem.setGoodsIntroduce(resultSet.getString("goods_introduce"));
        orderItem.setGoodsNum(resultSet.getInt("goods_num"));
        orderItem.setGoodsUnit(resultSet.getString("goods_unit"));
        orderItem.setGoodsPrice(resultSet.getInt("goods_price"));
        orderItem.setGoodsDiscount(resultSet.getInt("goods_discount"));
    }

    private void parseOrder(Order order, ResultSet resultSet) throws SQLException {
        order.setId(resultSet.getString("id"));
        order.setAccount_id(resultSet.getInt("account_id"));
        order.setAccount_name(resultSet.getString("account_name"));
        order.setCreate_time(resultSet.getString("create_time"));
        order.setFinish_time(resultSet.getString("finish_time"));
        order.setActual_amount(resultSet.getInt("actual_amount"));
        order.setTotal_money(resultSet.getInt("total_money"));
        order.setOrder_status(OrderStatus.valueOf(resultSet.getInt("order_status")));
    }

    /**
     * 读取文件中的SQL语句
     * @param s
     * @return
     */
    private String getSql(String s) {
        InputStream is = BrowseOrderServlet.class.getClassLoader().getResourceAsStream(s);
        StringBuilder sb = new StringBuilder();

        if(is == null)
            throw new RuntimeException("文件加载失败");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String line;
        try {
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("转化sql语句发生异常");
        }
    }
}
