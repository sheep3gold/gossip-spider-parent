package cn.itcast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JDKGet {
    public static void main(String[] args) throws Exception {
        //确定首页的url
        String indexUrl = "http://www.itcast.cn?username=xiaochaun";

        //发送请求，获取数据
        URL url = new URL(indexUrl);

        //通过url对象获取远程连接
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        //设置请求方式 请求参数 请求头
        //设置请求方式要大写，默认请求方式是get
        urlConnection.setRequestMethod("GET");

        //获取数据：原生API操作获取响应体的数据都是通过流的形式来获取
        InputStream in = urlConnection.getInputStream();

        int len = 0;
        byte[] b = new byte[1024];

        while ((len = in.read(b)) != -1) {
            System.out.println(new String(b, 0, len));
        }
        in.close();

    }
}
