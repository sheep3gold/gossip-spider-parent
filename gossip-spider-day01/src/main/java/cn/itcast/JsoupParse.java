package cn.itcast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import java.io.IOException;

public class JsoupParse {
    /**
     * 使用原生js的方式
     * @throws Exception
     */
    @Test
    public void jsoupIfJs() throws Exception {
        String indexUrl = "http://www.itcast.cn";
        //发送请求，获取数据
        Document document = Jsoup.connect(indexUrl).get();

        Elements divEls = document.getElementsByClass("nav_txt");
        Element divEl = divEls.get(0);

        Elements ulEls = divEl.getElementsByTag("ul");
        Element ulEl = ulEls.get(0);
        Elements liEls = ulEl.getElementsByTag("li");

        for (Element liEl : liEls) {
            Elements aEls = liEl.getElementsByTag("a");
            Element aEl = aEls.get(0);
            String text = aEl.text();
            System.out.println(text);
        }
    }

    /**
     * 使用选择器的方式
     * @throws Exception
     */
    @Test
    public void jsoupIfSelector() throws Exception {
        String indexUrl = "http://www.itcast.cn";

        Document document = Jsoup.connect(indexUrl).get();

        Elements aEls = document.select(".nav_txt>ul>li>a");
        for (Element aEl : aEls) {
            System.out.println(aEl.text());
        }
    }


}
