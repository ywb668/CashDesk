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

@WebServlet("/updateGoods")
public class GoodsUpdateServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("修改了");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        Integer id = Integer.parseInt(req.getParameter("goodsID"));
        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        Integer stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");
        Integer price = new Double(Double.parseDouble(req.getParameter("price")) * 100).intValue();
        Integer discount = Integer.parseInt(req.getParameter("discount"));

        System.out.println(id);
        System.out.println(name);
        System.out.println(introduce);
        System.out.println(stock);
        System.out.println(unit);
        System.out.println(price);
        System.out.println(discount);

        Goods goods = getGoods(id);
        System.out.println(goods);
        if(goods == null) {
            System.out.println("修改失败，商品不存在");
            PrintWriter writer = resp.getWriter();
            writer.write("<h2>要修改的商品不存在</h2>");
            writer.write("<a href=\"index.html\">返回主页</a>");
            writer.close();
        } else {
            String sql = "update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id=?";
            try(Connection connection = DBUtil.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, name);
                statement.setString(2, introduce);
                statement.setInt(3, stock);
                statement.setString(4, unit);
                statement.setInt(5, price);
                statement.setInt(6, discount);
                statement.setInt(7, id);

                if(statement.executeUpdate() == 1) {
                    //修改成功，跳转浏览页面
                    resp.sendRedirect("goodsbrowse.html");
                } else {
                    //修改失败,返回主页
                    PrintWriter writer = resp.getWriter();
                    writer.write("<h2>修改失败</h2>");
                    writer.write("<a href=\"index.html\">返回主页</a>");
                    writer.close();
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private Goods getGoods(Integer id) {
        String sql = "select id,name,introduce,stock,unit,price,discount from goods where id=?";

        Goods goods = null;
        try(Connection connection = DBUtil.getConnection();
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
