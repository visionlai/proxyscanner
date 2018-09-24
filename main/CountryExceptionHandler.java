package main;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author vision
 * 获取ip所属国家时网站出现不明原因的500响应，
 * 处理异常时将异常ip写入了exception.log文件,
 * 此类用于重新处理异常ip，并将处理结果写入数据库
 */
public class CountryExceptionHandler {

    public static void main(String[] args) {
        try (BufferedReader reader = new BufferedReader(new FileReader("/root/Desktop/exception.log"))) {
            Connection con;
            String ip = reader.readLine();

            // 文件不为空
            if (ip != null) {

                // 删除异常的记录或更新所属国家
                con = DBConnection.getConnection();
                if (con != null) {
                    try {
                        PreparedStatement deleteStatement = con.prepareStatement("DELETE FROM proxy WHERE ip=?");
                        PreparedStatement updateStatement = con.prepareStatement("UPDATE proxy SET country=? WHERE ip=?");

                        // 循环获取ip
                        while (ip != null) {
                            // 获取ip所属国家
                            String country = IPCountryGetter.getIPCountry(ip);

                            // 删除异常记录
                            if (country == null) {
                                deleteStatement.setString(1, ip);
                                deleteStatement.executeUpdate();
                            } else {  // 更新所属国家
                                updateStatement.setString(1, country);
                                updateStatement.setString(2, ip);
                                updateStatement.executeUpdate();
                            }

                            ip = reader.readLine();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            con.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
