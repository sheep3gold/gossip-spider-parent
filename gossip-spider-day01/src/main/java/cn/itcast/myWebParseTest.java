package cn.itcast;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class myWebParseTest {

    @Test
    public void TestMyWeb() throws Exception {
        String indexUrl = "http://www.baidu.com";
        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(indexUrl);
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");
        CloseableHttpResponse response = httpClient.execute(httpGet);

        String html = EntityUtils.toString(response.getEntity(), "UTF-8");

        Document document = Jsoup.parse(html);

        Elements trs = document.select("#head .s_form_wrapper");
        Elements selects = trs.select("style+div");
//        Element tr = trs.get(0);
        System.out.println(trs);
//        System.out.println(selects);
//        for (Element input : inputs) {
//            System.out.println(input.toString());
//        }
    }
}
