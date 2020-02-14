package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 连接数据库
 */
public class DBUtil {

    private static volatile DataSource dataSource;

    private DBUtil() {}

    public static DataSource getInstance() {
        if(dataSource == null) {
            synchronized (DBUtil.class) {
                if(dataSource == null) {
                    initDataSource();
                }
            }
        }
        return dataSource;
    }

    private static void initDataSource() {
        //InputStream is = DBUtil.class.getClassLoader().getResourceAsStream("mysqlConfig.properties");
        dataSource = new MysqlDataSource();
        ((MysqlDataSource)dataSource).setURL("jdbc:mysql://localhost:3306/cash?useUnicode=true&characterEncoding=UTF8");
        ((MysqlDataSource)dataSource).setUser("root");
        ((MysqlDataSource)dataSource).setPassword("123456");
    }

    public static Connection getConnection() throws SQLException {
        return getConnection(true);
    }

    public static Connection getConnection(boolean b) throws SQLException {
        Connection connection = getInstance().getConnection();
        connection.setAutoCommit(b);
        return connection;
    }

    public static void close() {}

}
