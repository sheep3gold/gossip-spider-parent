package cn.itcast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;

public class JsoupDocument {
    public static void main(String[] args) throws Exception {
        String indexUrl = "http://www.itcast.cn";

        //解析数据,获取document方式一
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>获取document的方式一</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
        Document document1 = Jsoup.parse(html);
        String title = document1.title();
        System.out.println(title);

        //方式二
        Document document2 = Jsoup.connect(indexUrl).get();
//        System.out.println(document2);
        //方式三,获取本地的html文件，转换document对象
//        Document document3 = Jsoup.parse(new File(""), "UTF-8");

        //方式四，指定一个HTML片段，获取document对象
        html = "<a>获取document的第四种方式</a>";
//        Document document4 = Jsoup.parseBodyFragment(html);
        Document document4 = Jsoup.parse(html);
        System.out.println(document4.text());
    }
}
