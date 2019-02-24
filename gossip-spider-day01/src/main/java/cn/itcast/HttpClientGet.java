package cn.itcast;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class HttpClientGet {
    public static void main(String[] args) throws Exception {
        String indexUrl = "http://www.itcast.cn?username=xiaochaun";

        //创建一个httpClient对象
        CloseableHttpClient httpClient = HttpClients.createDefault();
        //设置请求方式:请求对象
        HttpGet httpGet = new HttpGet(indexUrl);
        //设置请求头
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
        //设置请求参数

        //发送请求,获取响应对象
        //CloseableHttpResponse:响应行 响应头 响应体
        CloseableHttpResponse response = httpClient.execute(httpGet);

        //获取响应行：状态码
        int statusCode = response.getStatusLine().getStatusCode();
        System.out.println(statusCode);
        if (statusCode == 200) {
            //获取响应头
            Header[] headers = response.getHeaders("Content-Type");
            String value = headers[0].getValue();
            System.out.println(value);

            //获取响应体：HttpEntity
            HttpEntity entity = response.getEntity();//响应体数据
            String html = EntityUtils.toString(entity, "UTF-8");//只适用于返回字符类型饿的

            System.out.println(html);
        }
        httpClient.close();

    }
}
