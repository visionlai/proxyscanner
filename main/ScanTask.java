package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author vision
 * 传入网段参数，扫描该网段的端口，将开放并且无密码的ip
 * 和所属国家插入数据库
 */
public class ScanTask implements Runnable {

    /**
     * scanAddr 要扫描的网段，比如192.168.1.0
     */
    private String scanAddr;
    /**
     * 用于保存每个网段的扫描结果
     */
    private Vector<HashMap<String, String>> maps;

    public ScanTask(String scanAddr, Vector<HashMap<String, String>> maps) {
        this.scanAddr = scanAddr;
        this.maps = maps;
    }

    /**
     * 根据scanAddr获取ip列表
     */
    private ArrayList<String> callNmap() throws IOException {
        String cmd = "nmap -Pn -sS -p 1080 -oG - " + scanAddr + "/24";
        // 执行扫描命令
        Process process = Runtime.getRuntime().exec(cmd);
        BufferedReader br = new BufferedReader(
                new InputStreamReader(
                        process.getInputStream()
                )
        );

        // 保存端口开放并且无密码的ip
        ArrayList<String> ipList = new ArrayList<String>();
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("open")) {
                String[] lines = line.split(" ");
                ipList.add(lines[1]);
            }
        }
        br.close();

        return ipList;
    }

    /**
     * 根据ip列表获取ip和所属国家的map
     */
    private HashMap<String, String> getIpAndCountry() throws IOException {
        ArrayList<String> ipList = callNmap();
        HashMap<String, String> map = new HashMap<String, String>();
        for (String ip: ipList) {
            String country = IPCountryGetter.getIPCountry(ip);
            if (country == null || !country.equals("unusable")) {
                map.put(ip, country);
            }
        }
        return map;
    }

    @Override
    public void run() {
        try {
            // 获取ip和所属国家的map
            HashMap<String, String> map = getIpAndCountry();

            if (map.size() != 0) {
                maps.add(map);
            }

            // 输出扫描日志
            System.out.println(scanAddr + " 扫描完成, 共找到 " + map.size() + " 个代理");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
