package main;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author vision
 * 将数据库中代理写入文件，便于chrome插件的导入
 */
public class ProxyWriter {

    /**
     * 将传入的代理ip包装成一定格式的json对象
     * @param proxyIP 代理ip
     * @param country 代理ip所属国家
     * @param num 代理编号
     */
    public static JSONObject getAnProxyJSON(String proxyIP, String country, int num) {
        // 设置不使用代理规则
        JSONArray bypassList = new JSONArray();
        JSONObject bypass1 = new JSONObject();
        bypass1.put("conditionType", "BypassCondition");
        bypass1.put("pattern", "127.0.0.1");
        bypassList.put(bypass1);
        JSONObject bypass2 = new JSONObject();
        bypass2.put("conditionType", "BypassCondition");
        bypass2.put("pattern", "[::1]");
        bypassList.put(bypass2);
        JSONObject bypass3 = new JSONObject();
        bypass3.put("conditionType", "BypassCondition");
        bypass3.put("pattern", "localhost");
        bypassList.put(bypass3);

        // 设置代理模式
        JSONObject fallbackProxy = new JSONObject();
        fallbackProxy.put("host", proxyIP);
        fallbackProxy.put("port", 1080);
        fallbackProxy.put("scheme", "socks5");

        // 新建代理
        JSONObject proxy = new JSONObject();
        proxy.put("bypassList", bypassList);
        proxy.put("fallbackProxy", fallbackProxy);
        proxy.put("color", "#ca0");
        proxy.put("name", country + num);
        proxy.put("profileType", "FixedProfile");
        proxy.put("revision", "166062851a2");

        return proxy;
    }

    public static void main(String[] args) {

        // 新建代理集合
        JSONObject proxies = new JSONObject();

        // 代理参数
        proxies.put("-addConditionsToBottom", true);
        proxies.put("-confirmDeletion", false);
        proxies.put("-downloadInterval", 1440);
        proxies.put("-enableQuickSwitch", false);
        proxies.put("-monitorWebRequests", true);
        proxies.put("-quickSwitchProfiles", new JSONArray());
        proxies.put("-refreshOnProfileChange", true);
        proxies.put("-revertProxyChanges", true);
        proxies.put("-showExternalProfile", true);
        proxies.put("-showInspectMenu", true);
        proxies.put("-startupProfileName", "");
        proxies.put("schemaVersion", 2);
        proxies.put("-showConditionTypes", 0);

        // 提取数据库中信息
        Connection con = DBConnection.getConnection();
        if (con != null) {
            try {
                PreparedStatement statement = con.prepareStatement("SELECT * FROM proxy");
                ResultSet result = statement.executeQuery();

                int num = 1;
                while (result.next()) {
                    String ip = result.getString("ip");
                    String country = result.getString("country");

                    // 获取一个代理
                    JSONObject proxy = getAnProxyJSON(ip, country, num);

                    // 代理集合添加代理
                    proxies.put("+" + country + num, proxy);
                    num++;
                }
                System.out.println(proxies);

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
