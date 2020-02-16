package servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 用于将商品id存入session，方便接下来的jsp页面对该商品信息的修改
 */
@WebServlet("/setUpdateSessionServlet")
public class SetUpdateSessionServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("自定义修改了");

        HttpSession session = req.getSession();
        Integer id = Integer.parseInt(req.getParameter("id"));
        session.setAttribute("id", id);

        resp.sendRedirect("updategoods.jsp");
}
}
