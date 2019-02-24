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

/**
 * 爬取起点中文网某一部小说的全部免费章节内容
 */
public class QiDianSpider {
    public static void main(String[] args) throws Exception {
        //确定首页URL:某一篇小说的第一章的URL
        String indexUrl="https://read.qidian.com/chapter/-9mO0GOUw_TywypLIF-xfQ2/hsQrkvuAncvgn4SMoDUcDQ2";

        while (true) {
            CloseableHttpClient httpClient = HttpClients.createDefault();

            //设置请求方式
            HttpGet httpGet = new HttpGet(indexUrl);

            //设置请求头
            httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36");

            //发送请求，获取响应
            CloseableHttpResponse response = httpClient.execute(httpGet);

            //获取数据
            String html = EntityUtils.toString(response.getEntity(), "UTF-8");

            //解析数据
            Document document = Jsoup.parse(html);

            //解析章节数据
            Elements chapterNameEls = document.select(".j_chapterName");
            System.out.println(chapterNameEls.text());
            Elements pEls = document.select("[class=read-content j_readContent] p");

            for (Element pEl : pEls) {
                System.out.println(pEl.text());
            }
            //下一章内容
            Elements aEl = document.select("#j_chapterNext[href*=chapter]");
            if (aEl == null || aEl.size() == 0) {
                break;
            }
            String nextUrl = aEl.attr("href");
            indexUrl = "https:" + nextUrl;

            //关闭httpClient对象
            httpClient.close();
        }
    }
}
