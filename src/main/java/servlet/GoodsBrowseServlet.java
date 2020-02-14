package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Goods;
import util.DBUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/browseGoods")
public class GoodsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("GoodsBrowseServlet start");
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");

        //TODO 查询数据库所有元素，并处理

        ResultSet resultSet = null;

        String sql = "select id,name,introduce,stock,unit,price,discount from goods";
        try(Connection connection = DBUtil.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            resultSet = statement.executeQuery();

            List<Goods> list = new ArrayList<>();
            while(resultSet.next()) {
                Goods goods = new Goods();
                goods.setId(resultSet.getInt("id"));
                goods.setName(resultSet.getString("name"));
                goods.setIntroduce(resultSet.getString("introduce"));
                goods.setStock(resultSet.getInt("stock"));
                goods.setUnit(resultSet.getString("unit"));
                goods.setPrice(resultSet.getInt("price"));
                goods.setDiscount(resultSet.getInt("discount"));

                list.add(goods);
            }
            System.out.println(list);
            //将list对转换为json，返回给前端
            //可以方便的将模型对象转换为json
            ObjectMapper mapper = new ObjectMapper();
            PrintWriter pw = resp.getWriter();
            //将list转换成json字符串，放到pw中
            mapper.writeValue(pw, list);

            Writer writer = resp.getWriter();
            writer.write(pw.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if(resultSet != null)
                    resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
