package cn.itcast;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class JDKPost {
    public static void main(String[] args) throws Exception {
        String indexUrl = "http://www.itcast.cn";

        URL url = new URL(indexUrl);

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("POST");

        urlConnection.setDoOutput(true);
        OutputStream out = urlConnection.getOutputStream();
        out.write("username=xiaochaun&password=123".getBytes());

        InputStream in = urlConnection.getInputStream();

        int len = 0;
        byte[] b = new byte[1024];
        while ((len = in.read(b)) != -1) {
            System.out.println(new String(b, 0, len));
        }
        in.close();
    }
}
