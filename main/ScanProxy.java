package main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author vision
 * 调用端口扫描模块，将ip和所属国家写入数据库
 */
public class ScanProxy {

    public static void main(String[] args) {
        // 计时
        long start = System.currentTimeMillis();

        // 保存所有线程的扫描结果，最后一次性写入数据库
        Vector<HashMap<String, String>> maps = new Vector<HashMap<String, String>>();

        // 多线程执行
        ExecutorService pool = Executors.newFixedThreadPool(100);

        for (int i = 0; i < 256; i++) {
            pool.execute(new ScanTask("172.29." + i + ".0", maps));
            pool.execute(new ScanTask("172.31." + i + ".0", maps));
        }

        // 关闭池
        pool.shutdown();

        // 等待线程池执行完毕
        while (!pool.isTerminated()) {
            try {
                Thread.sleep(60000);

                // 避免单个线程的超时，运行一定时间后强制退出
                long end = System.currentTimeMillis();
                int cost = (int) (end - start) / 60000;

                // 经过测试，大部分扫描线程2分钟内执行完毕
                if (cost >= 2) {
                    break;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("扫描完成，正在操作数据库...");
        Connection con = DBConnection.getConnection();
        if (con != null) {
            PreparedStatement statement;
            try {

                // 删除proxy表中所有记录
                statement = con.prepareStatement("DELETE FROM proxy");
                statement.executeUpdate();

                // 将maps中所有代理插入数据库
                statement = con.prepareStatement("INSERT INTO proxy VALUES(?, ?)");
                for (HashMap<String, String> map: maps) {
                    for (Map.Entry<String, String> mapper: map.entrySet()) {
                        statement.setString(1, mapper.getKey());
                        statement.setString(2, mapper.getValue());
                        statement.executeUpdate();
                    }
                }

                // 输入插入数据库日志
                System.out.println("已将 " + maps.size() + " 个网段所有代理插入数据库");

                System.exit(0);
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
}
