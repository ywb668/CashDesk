package servlet;

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
import java.sql.SQLException;

@WebServlet("/inbound")
public class GoodsPutAwayServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String name = req.getParameter("name");
        String introduce = req.getParameter("introduce");
        Integer stock = Integer.parseInt(req.getParameter("stock"));
        String unit = req.getParameter("unit");
        Integer price = new Double(Double.parseDouble(req.getParameter("price")) * 100).intValue();
        Integer discount = Integer.parseInt(req.getParameter("discount"));

        System.out.println(name);
        System.out.println(introduce);
        System.out.println(stock);
        System.out.println(unit);
        System.out.println(price);
        System.out.println(discount);

        //TODO 插入数据库
        String sql = "insert into goods(name,introduce,stock,unit,price,discount) values (?,?,?,?,?,?)";
        try(Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setString(2, introduce);
            statement.setInt(3, stock);
            statement.setString(4, unit);
            statement.setInt(5, price);
            statement.setInt(6, discount);

            int i = statement.executeUpdate();

            if(i == 1) {
                PrintWriter pw = resp.getWriter();
                pw.write("<a href=\"inbound.html\">上架成功,重新上架</a>");
                pw.close();
                //跳转到浏览商品信息页面
                //resp.sendRedirect("");
            } else {
                PrintWriter pw = resp.getWriter();
                pw.write("<a href=\"inbound.html\">上架失败,重新上架</a>");
                pw.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
