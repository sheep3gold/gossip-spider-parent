package cn.itheima.spider.version2.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class HttpClientUtils {
    private static PoolingHttpClientConnectionManager connectionManager;

    static {
        //定义一个连接池的工具类对象
        connectionManager = new PoolingHttpClientConnectionManager();
        //定义连接池属性
        //定义连接池最大的连接数
        connectionManager.setMaxTotal(200);
        //定义主机的最大的并发数
        connectionManager.setDefaultMaxPerRoute(20);
    }

    //获取closeHttpClient
    private static CloseableHttpClient getCloseableHttpClient() {

        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(connectionManager).build();

        return httpClient;
    }


    //执行请求返回HTML页面,代理ip
    /*private static String execute(HttpRequestBase httpRequestBase) throws Exception {

        httpRequestBase.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
        *//**
         * setConnectionRequestTimeout:设置获取请求的最长时间
         *
         * setConnectTimeout: 设置创建连接的最长时间
         *
         * setSocketTimeout: 设置传输超时的最长时间
         *//*

        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
                .setSocketTimeout(10 * 1000).build();

        httpRequestBase.setConfig(config);


//        CloseableHttpClient httpClient = getCloseableHttpClient();

//        CloseableHttpResponse response = httpClient.execute(httpRequestBase);
        String html = null;

        //从redis中获取ip
        Jedis jedis = JedisUtils.getJedis();
        //redis中ip个数少于3个则向redis中添加代理ip
        if (jedis.llen("spider:ip") < 1) {
            setDynamicIpToRedis();
        }
        List<String> ipkv = jedis.brpop(0, "spider:ip");

        CloseableHttpClient httpClient = getProxyHttpClient(ipkv.get(1));
        try {
            CloseableHttpResponse response = httpClient.execute(httpRequestBase);


            if (response.getStatusLine().getStatusCode() == 200) {
                html = EntityUtils.toString(response.getEntity(), "UTF-8");
                System.out.println("ok");
                //请求成功后，将代理ip放回去，下次继续使用
                jedis.lpush("spider:ip", ipkv.get(1));
                jedis.close();
            } else if (response.getStatusLine().getStatusCode() == 404) {
                return html;
            }
        } catch (Exception e) {
            System.out.println("请求失败");
        }

        *//*if (response.getStatusLine().getStatusCode() == 200) {
            html = EntityUtils.toString(response.getEntity());
        }*//*
        //自动重试
        if (html == null) {
            return execute(httpRequestBase);
        }else {
            return html;
        }
    }*/

    //执行请求返回HTML页面
    private static String execute(HttpRequestBase httpRequestBase) throws IOException {

        httpRequestBase.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
        /**
         * setConnectionRequestTimeout:设置获取请求的最长时间
         *
         * setConnectTimeout: 设置创建连接的最长时间
         *
         * setSocketTimeout: 设置传输超时的最长时间
         */

        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
                .setSocketTimeout(10 * 1000).build();

        httpRequestBase.setConfig(config);


        CloseableHttpClient httpClient = getCloseableHttpClient();

        CloseableHttpResponse response = httpClient.execute(httpRequestBase);
        String html;
        if(response.getStatusLine().getStatusCode()==200){
            html = EntityUtils.toString(response.getEntity(), "GBK");
        }else{
            html = null;
        }



        return html;
    }

    //代理ip
    private static CloseableHttpClient getProxyHttpClient(String ipkv) {
        String[] vals = ipkv.split(":");
//        System.out.println(Arrays.toString(vals));
        HttpHost proxy = new HttpHost(vals[0], Integer.parseInt(vals[1]));
        DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
        return HttpClients.custom().setConnectionManager(connectionManager)
                .setRoutePlanner(routePlanner).build();
    }

    //get请求执行
    public static String doGet(String url) throws Exception {
        HttpGet httpGet = new HttpGet(url);

        String html = execute(httpGet);

        return html;

    }

    //post请求执行
    public static String doPost(String url, Map<String, String> param) throws Exception {
        HttpPost httpPost = new HttpPost(url);

        List<BasicNameValuePair> list = new ArrayList<BasicNameValuePair>();

        for (String key : param.keySet()) {

            list.add(new BasicNameValuePair(key, param.get(key)));
        }
        HttpEntity entity = new UrlEncodedFormEntity(list);
        httpPost.setEntity(entity);

        return execute(httpPost);
    }

    //获取代理ip并存入redis
    public static void setDynamicIpToRedis() throws Exception {
        String indexUrl = "https://proxy.horocn.com/api/proxies?order_id=XVU91623091510383673&num=20&format=text&line_separator=win&can_repeat=no";
        CloseableHttpClient httpClient = getCloseableHttpClient();
        HttpGet httpGet = new HttpGet(indexUrl);
        CloseableHttpResponse response = httpClient.execute(httpGet);
        String html = EntityUtils.toString(response.getEntity());
        Document document = Jsoup.parse(html);
        Elements pre = document.select("body");
//        System.out.println(pre.text());
        if (pre == null) {
            Thread.sleep(10000);
            setDynamicIpToRedis();
            pre = new Elements();
        }
        String ips = pre.text();
        String[] ipArr = ips.split(" ");
        System.out.println(Arrays.toString(ipArr));
        Jedis jedis = JedisUtils.getJedis();
        for (String ip : ipArr) {
            jedis.lpush("spider:ip", ip);
        }
//        String ip = pre.text();
//        System.out.println(ips);
    }
}
