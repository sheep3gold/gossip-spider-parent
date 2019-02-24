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

import java.io.IOException;

public class RankBooksSpider {
    public static void main(String[] args) throws Exception {
        String indexUrl = "https://www.qidian.com/rank/yuepiao";
        HttpGet httpGet = new HttpGet(indexUrl);
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("Cookie", "_csrfToken=06sNjroGpPMkeJ1YxXjkwTj4wgXXXXdDQlN5w72G; newstatisticUUID=1545477442_1022469316; e1=%7B%22pid%22%3A%22qd_P_rank_01%22%2C%22eid%22%3A%22qd_C45%22%2C%22l1%22%3A5%7D; e2=%7B%22pid%22%3A%22qd_P_xianxia%22%2C%22eid%22%3A%22qd_A53%22%2C%22l1%22%3A40%7D");
        httpGet.setHeader("Referer", "https://www.qidian.com/rank");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = httpClient.execute(httpGet);

        String html = EntityUtils.toString(response.getEntity());
        Document document = Jsoup.parse(html);

        Elements rankEls = document.select(".book-img-text li");
        for (Element rankEl : rankEls) {
            String num = rankEl.attr("data-rid");
            Elements namesEl = rankEl.select(".book-mid-info>h4>a");
            Element nameEl = namesEl.get(0);
            String name = nameEl.text();
            System.out.println(num + ": " + name);
        }
    }
}
