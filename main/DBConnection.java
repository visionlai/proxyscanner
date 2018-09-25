package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author vision
 * 获取数据库连接
 */
public class DBConnection {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/szu";
    private static final String USER = "vision";
    private static final String PASS = "lzq123456";

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
            Class.forName(DBDRIVER);
            Connection con = DriverManager.getConnection(DBURL, USER, PASS);
            return con;
    }
}
