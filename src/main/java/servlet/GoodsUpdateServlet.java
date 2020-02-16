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
        //System.out.println("修改了");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        Goods goods = new Goods();
        goods.setId(Integer.parseInt(req.getParameter("goodsID")));
        goods.setName(req.getParameter("name"));
        goods.setIntroduce(req.getParameter("introduce"));
        goods.setStock(Integer.parseInt(req.getParameter("stock")));
        goods.setUnit(req.getParameter("unit"));
        goods.setPrice(new Double(Double.parseDouble(req.getParameter("price")) * 100).intValue());
        goods.setDiscount(Integer.parseInt(req.getParameter("discount")));

        //调用getGoods方法查看是否存在该id商品
        if (getGoods(goods.getId()) == null) {
            //表示仓库中没有给id的商品，跳转操作失败页面
            resp.sendRedirect("updateGoodsError.html");

        } else {
            if (updateGoods(goods)) {
                //修改成功，跳转浏览页面
                resp.sendRedirect("goodsbrowse.html");
            } else {
                //修改失败,返回主页
                resp.sendRedirect("updateGoodsError2.html");
            }
        }
    }

    private boolean updateGoods(Goods goods) {
        String sql = "update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goods.getName());
            statement.setString(2, goods.getIntroduce());
            statement.setInt(3, goods.getStock());
            statement.setString(4, goods.getUnit());
            statement.setInt(5, goods.getPriceInt());
            statement.setInt(6, goods.getDiscount());
            statement.setInt(7, goods.getId());
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库修改失败");
        }
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
