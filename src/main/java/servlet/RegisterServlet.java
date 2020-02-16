package servlet;

import entity.Account;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        String username = req.getParameter("username");
        String password = req.getParameter("password");

        //判断数据库中是否有该用户名的用户
        //返回true表示存在，则无法重新注册，跳转注册失败页面
        //返回false表示不存在，则可以正常注入，将用户信息存入数据库，并返回登录界面
        boolean flag = existAccount(username);

        //TODO 暂时缺少对用户名和密码为空处理的情况
        //可以通过转发到一个新的静态页面
        //可以传输标识信息有前端给出相应的动态页面

        if (flag) {

            resp.sendRedirect("registerError.html");
        } else {
            //成功注册后跳转到登录界面
            if (registerAccount(username, password))
                resp.sendRedirect("login.html");
        }
    }

    private boolean registerAccount(String username, String password) {

        String sql = "insert into account(username, password) values (?, ?)";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            statement.setString(2, password);
            return statement.executeUpdate() != 0;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库插入数据失败");
        }
    }

    private boolean existAccount(String username) {

        String sql = "select username,password from account where username=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库查询失败");
        }
    }
}
