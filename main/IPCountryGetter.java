package main;

import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * @author vision
 * 获取代理所属国家
 */
public class IPCountryGetter {

    /**
     * @param proxyIP 代理ip
     * @return 代理ip所属国家
     * @throws IOException
     */
    public static String getIPCountry(String proxyIP){
        try {
            // 此网站可获取ip所属国家
            Connection con = Jsoup.connect("https://ip.cn");
            con.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
            con.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
            // 设置代理
            Document doc = con.proxy(proxyIP, 1080).get();
            String country = doc.getElementsByTag("code").get(1).text();
            return country;
        } catch (SocketException | SocketTimeoutException e) {  // 代理需要密码引发的异常
            return "unusable";
        } catch (IOException e) {   // 查询网站异常
            try {
                // 换另一个地址
                Connection con = Jsoup.connect("http://2018.ip138.com/ic.asp");
                con.header("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
                con.header("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
                // 设置代理
                Document doc = con.proxy(proxyIP, 1080).get();
                String center = doc.body().text();
                String country = center.split("来自：")[1];
                return country;
            } catch (HttpStatusException e1) {
                return "unusable";
            } catch (IOException e1) {
                System.err.println(proxyIP);
                return null;
            } catch (Exception e1) {
                return "unusable";
            }
        }
    }

}
