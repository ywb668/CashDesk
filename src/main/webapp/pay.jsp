<%@ page import="entity.Goods" %>
<%@ page import="java.sql.Connection" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="util.DBUtil" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="java.sql.SQLException" %>
<%--
  Created by IntelliJ IDEA.
  User: LENOVO
  Date: 2020/2/16
  Time: 14:19
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>

<head>
    <title>购买商品</title>
    <link rel="stylesheet"/>
    <link rel="stylesheet" href="css/Site.css"/>
    <link rel="stylesheet" href="css/zy.all.css"/>
    <link rel="stylesheet" href="css/font-awesome.min.css"/>
    <link rel="stylesheet" href="css/amazeui.min.css"/>
    <link rel="stylesheet" href="css/admin.css"/>

</head>

<body>
<%
    HttpSession httpSession = request.getSession();
    int id = (Integer)httpSession.getAttribute("id");

    /*根据id查出对应的商品信息*/
    String sql = "select id,name,introduce,stock,unit,price,discount from goods where id=?";

    Goods goods = new Goods();
    try(Connection connection = DBUtil.getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {
        statement.setInt(1, id);
        try(ResultSet resultSet = statement.executeQuery()) {
            if(resultSet.next()) {
                goods.setId(id);
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
%>
<div class="dvcontent">
    <div>
        <!--tab start-->
        <div class="taborder" style="margin: 30px;">
            <div class="hd">
                <ul>
                    <li class="" style="box-sizing: initial;-webkit-box-sizing: initial;">支付订单</li>
                </ul>
            </div>
            <div class="bd">
                <ul class="theme-popbod dform" style="display: none;">
                    <div class="am-cf admin-main" style="padding-top: 0px;">
                        <!-- content start -->

                        <div class="am-cf admin-main" style="padding-top: 0px;">
                            <!-- content start -->
                            <div class="admin-content">
                                <div class="admin-content-body">

                                    <div class="am-g">
                                        <div class="am-u-sm-12 am-u-md-4 am-u-md-push-8">

                                        </div>
                                        <div class="am-u-sm-12 am-u-md-8 am-u-md-pull-4"
                                             style="padding-top: 70px;">
                                            <form class="am-form am-form-horizontal"
                                                  action="payServlet" method="post">

                                                <div class="am-form-group">
                                                    <label for="goodsID" class="am-u-sm-3 am-form-label">
                                                        购买商品的ID</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsID" required
                                                               placeholder="请输入你要购买的货物id" value="<%=goods.getId()%>" name="goodsID">
                                                        <small>需购买商品的ID</small>
                                                    </div>

                                                    <label for="goodsName" class="am-u-sm-3 am-form-label">
                                                        购买商品的名称</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsName" required
                                                               value="<%=goods.getName()%>" name="goodsName">
                                                        <small>需购买商品的名称</small>
                                                    </div>

                                                    <label for="goodsPrice" class="am-u-sm-3 am-form-label">
                                                        购买商品的价格</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsPrice" required
                                                               value="<%=goods.getPrice()%>" name="goodsPrice">
                                                        <small>需购买商品的价格(单位:元)</small>
                                                    </div>

                                                    <label for="goodsNum" class="am-u-sm-3 am-form-label">
                                                        需购买商品的数量</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsNum" required
                                                               placeholder="请输入你要购买的货物数量" name="goodsNum">
                                                        <small>需购买商品的数量</small>
                                                    </div>
                                                </div>

                                                <div class="am-form-group">
                                                    <div class="am-u-sm-9 am-u-sm-push-3">
                                                        <input type="submit" class="am-btn am-btn-success"
                                                               value="购买商品"/>
                                                        <input type="button" class="am-btn am-btn-success"
                                                               value="商品列表" onclick="toGoods()"/>
                                                        <input type="button" class="am-btn am-btn-success"
                                                               value="返回首页" onclick="toMenu()"/>
                                                    </div>
                                                </div>
                                            </form>
                                        </div>
                                    </div>

                                </div>
                                <!-- content end -->
                            </div>
                        </div>
                    </div>
                    <!--tab end-->
                </ul>

                <script src="js/jquery-1.7.2.min.js" type="text/javascript"></script>
                <script src="js/plugs/Jqueryplugs.js" type="text/javascript"></script>
                <script src="js/_layout.js"></script>
                <script src="js/plugs/jquery.SuperSlide.source.js"></script>
                <script>
                    // var num = 1;
                    $(function () {

                        $(".taborder").slide({trigger: "click"});

                    });
                    function toMenu() {
                        window.location.href="index.html";
                    }
                    function toGoods() {
                        window.location.href="goodsbrowse.html";
                    }
                </script>
            </div>
        </div>
    </div>
</div>
</body>
</html>
