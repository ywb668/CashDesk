package servlet;

import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/inbound")
public class GoodsPutAwayServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        Goods goods = new Goods();
        goods.setName(req.getParameter("name"));
        goods.setIntroduce(req.getParameter("introduce"));
        goods.setStock(Integer.parseInt(req.getParameter("stock")));
        goods.setUnit(req.getParameter("unit"));
        goods.setPrice(new Double(Double.parseDouble(req.getParameter("price")) * 100).intValue());
        goods.setDiscount(Integer.parseInt(req.getParameter("discount")));

        /*System.out.println(name);
        System.out.println(introduce);
        System.out.println(stock);
        System.out.println(unit);
        System.out.println(price);
        System.out.println(discount);*/

        //判断该商品名的商品是否已经上架
        //如果已经上架就跳转上架失败页面
        //如果是新的商品就正常上架，插入数据库
        boolean flag = existGoods(goods.getName());

        if (flag) {
            //表示仓库中已有同商品名的商品
            resp.sendRedirect("inboundError.html");
        } else {
            //表示这是一个新的商品，可以正常上架

            //判断是否上架成功
            if(inboundGoods(goods)) {
                //跳转上架成功页面
                resp.sendRedirect("inboundSuccess.html");
            } else {
                //跳转上架失败页面
                resp.sendRedirect("inboundError.html");
            }
        }
    }

    private boolean inboundGoods(Goods goods) {
        String sql = "insert into goods(name,introduce,stock,unit,price,discount) values (?,?,?,?,?,?)";
        try(Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goods.getName());
            statement.setString(2, goods.getIntroduce());
            statement.setInt(3, goods.getStock());
            statement.setString(4, goods.getUnit());
            statement.setInt(5, goods.getPriceInt());
            statement.setInt(6, goods.getDiscount());

            return statement.executeUpdate() == 1;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库插入失败");
        }
    }

    private boolean existGoods(String name) {
        String sql = "select id,name,introduce,stock,unit,price,discount from goods where name=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库查询失败");
        }
    }
}
