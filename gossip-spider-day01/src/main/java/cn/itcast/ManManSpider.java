package cn.itcast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * 模拟登陆慢慢买这个网址, 登陆成功以后, 获取当前用户的积分信息
 */
public class ManManSpider {
    public static void main(String[] args) throws Exception {
        String loginUrl = "http://home.manmanbuy.com/login.aspx";

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(loginUrl);

        //封装请求参数
        List<BasicNameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("__VIEWSTATE", "/wEPDwULLTIwNjQ3Mzk2NDFkGAEFHl9fQ29udHJvbHNSZXF1aXJlUG9zdEJhY2tLZXlfXxYBBQlhdXRvTG9naW4voj01ABewCkGpFHsMsZvOn9mEZg=="));
        list.add(new BasicNameValuePair("__EVENTVALIDATION", "/wEWBQLW+t7HAwLB2tiHDgLKw6LdBQKWuuO2AgKC3IeGDJ4BlQgowBQGYQvtxzS54yrOdnbC"));
        list.add(new BasicNameValuePair("txtUser", "itcast"));
        list.add(new BasicNameValuePair("txtPass", "www.itcast.cn"));
        list.add(new BasicNameValuePair("btnLogin", "登陆"));

        HttpEntity entity = new UrlEncodedFormEntity(list);
        httpPost.setEntity(entity);

        //封装请求头：referer
        httpPost.setHeader("Referer", "http://home.manmanbuy.com/login.aspx");

        //发送请求获取响应对象
        CloseableHttpResponse response = httpClient.execute(httpPost);

        //状态码
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 302) {
            //登陆成功，获取重定向URL
            Header[] locations = response.getHeaders("Location");
            String reUrl = locations[0].getValue();
            reUrl = "http://home.manmanbuy.com" + reUrl;
            Header[] cookies = response.getHeaders("Set-Cookie");
            httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(reUrl);
            //封装登陆成功的cookie标识信息
            httpGet.setHeader("Cookie",cookies[0].getValue()+" "+cookies[1].getValue());
            //重定向后的response的对象
            response = httpClient.execute(httpGet);
            //重定向后的页面数据
            String html = EntityUtils.toString(response.getEntity(), "UTF-8");

            //解析HTML的数据
            Document document = Jsoup.parse(html);
            Elements jiFenEl = document.select("#aspnetForm > div.udivright > div:nth-child(2) > table > tbody > tr > td:nth-child(1) > table:nth-child(2) > tbody > tr > td:nth-child(2) > div:nth-child(1) > font");
            System.out.println(jiFenEl.text());

        }
    }
}
