package servlet;

import entity.Account;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //System.out.println("LoginServlet");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        //获取用户名和密码
        String userName = req.getParameter("username");
        String password = req.getParameter("password");

        //根据用户名和密码确认是否存在该用户
        //存在即正确登录跳转到菜单界面
        //否则登录失败跳转失败页面
        String sql = "select id,username,password from account where username=? and password=?";
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
             ) {
            statement.setString(1, userName);
            statement.setString(2, password);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<Account> accounts = new ArrayList<>();
                while (resultSet.next()) {
                    Account account = new Account();
                    account.setId(resultSet.getInt("id"));
                    account.setUserName(resultSet.getString("username"));
                    account.setPassword(resultSet.getString("password"));
                    accounts.add(account);
                }
                if(accounts.size() == 1) {
                    //将用户信息通过session保存
                    HttpSession session = req.getSession();
                    session.setAttribute("user", accounts.get(0));
                    // 登录成功 跳转菜单界面
                    resp.sendRedirect("index.html");
                } else {
                    //用户名或密码错误 给出提示界面
                    resp.sendRedirect("loginError.html");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
