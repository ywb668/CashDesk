<%@ page import="java.sql.Connection" %>
<%@ page import="util.DBUtil" %>
<%@ page import="java.sql.PreparedStatement" %>
<%@ page import="java.sql.SQLException" %>
<%@ page import="java.sql.ResultSet" %>
<%@ page import="entity.Goods" %><%--
  Created by IntelliJ IDEA.
  User: LENOVO
  Date: 2020/2/12
  Time: 21:07
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
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
        <div class="tabs3" style="margin: 0px;">
            <div class="hd">
                <ul>
                    <li class="" style="box-sizing: initial;-webkit-box-sizing: initial;">商品信息更新</li>
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
                                             style="padding-top: 30px;">
                                            <form class="am-form am-form-horizontal"
                                                  action="updateGoods" method="post">

                                                <div class="am-form-group">
                                                    <label for="goodsID" class="am-u-sm-3 am-form-label">
                                                        需更新商品ID</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="goodsID" required
                                                               value="<%=goods.getId()%>" name="goodsID">
                                                        <small>商品ID</small>
                                                    </div>
                                                </div>

                                                <div class="am-form-group">
                                                    <label for="name" class="am-u-sm-3 am-form-label">
                                                        商品名称</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="name" required
                                                               placeholder="商品名称" value="<%=goods.getName()%>" name="name">
                                                        <small>商品</small>
                                                    </div>
                                                </div>

                                                <div class="am-form-group">
                                                    <label for="stock" class="am-u-sm-3 am-form-label">
                                                        需更新库存</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="stock" required
                                                               placeholder="库存" value="<%=goods.getStock()%>" name="stock">
                                                        <small>库存</small>
                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="introduce" class="am-u-sm-3 am-form-label">
                                                        需更新商品介绍</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="introduce" required
                                                               placeholder="商品介绍" value="<%=goods.getIntroduce()%>" name="introduce">
                                                        <small>商品介绍</small>
                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="unit" class="am-u-sm-3 am-form-label">
                                                        需更新商品单位</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="unit" required
                                                               placeholder="商品单位" value="<%=goods.getUnit()%>" name="unit">
                                                        <small>商品单位</small>
                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="price" class="am-u-sm-3 am-form-label">
                                                        需更新商品价格</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="price" required
                                                               placeholder="商品价格" value="<%=goods.getPrice()%>" name="price">
                                                        <small>商品价格(单位:元)</small>
                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <label for="discount" class="am-u-sm-3 am-form-label">
                                                        需更新商品折扣</label>
                                                    <div class="am-u-sm-9">
                                                        <input type="text" id="discount" required
                                                               placeholder="商品折扣" value="<%=goods.getDiscount()%>" name="discount">
                                                        <small>商品折扣</small>
                                                    </div>
                                                </div>
                                                <div class="am-form-group">
                                                    <div class="am-u-sm-9 am-u-sm-push-3">
                                                        <input type="submit" class="am-btn am-btn-success"
                                                               value="更新商品"/>
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
                    $(function () {
                        $(".tabs3").slide({trigger: "click"});
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
