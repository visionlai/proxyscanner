package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author vision
 */
public class DBConnection {

    private static final String DBDRIVER = "com.mysql.jdbc.Driver";
    private static final String DBURL = "jdbc:mysql://localhost:3306/szu";
    private static final String USER = "root";
    private static final String PASS = "123456";
    private static Connection con = null;

    /**
     * 获取数据库连接
     */
    public static Connection getConnection() {
        try {
            Class.forName(DBDRIVER);
            con = DriverManager.getConnection(DBURL, USER, PASS);
            return con;
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            return con;
        }
    }
}
