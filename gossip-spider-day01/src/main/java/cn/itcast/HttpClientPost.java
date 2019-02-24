package cn.itcast;

import org.apache.http.HttpEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class HttpClientPost {
    public static void main(String[] args) throws Exception {
        String indexUrl = "http://www.itcast.cn";

        //创建httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();

        //设置请求url
        HttpPost httpPost = new HttpPost(indexUrl);

        List<BasicNameValuePair> list = new ArrayList<>();
        list.add(new BasicNameValuePair("username", "xiaochaun"));
        list.add(new BasicNameValuePair("password", "123"));
        HttpEntity entity = new UrlEncodedFormEntity(list);
        httpPost.setEntity(entity);

        //发送请求，获取响应
        CloseableHttpResponse response = httpClient.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            System.out.println(EntityUtils.toString(response.getEntity(), "UTF-8"));
        }
        httpClient.close();

    }
}
