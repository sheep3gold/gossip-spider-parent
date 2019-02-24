package cn.itheima.spider.version2.gossip;

import cn.itheima.spider.version2.pojo.News;
import cn.itheima.spider.version2.utils.HttpClientUtils;
import cn.itheima.spider.version2.utils.IdWorker;
import cn.itheima.spider.version2.utils.JedisUtils;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 从redis中获取新闻详情页的url，解析url，封装news对象,将news对象转换为json
 * 数据,保存到redis
 */
public class News163Slave {
    public static void main(String[] args) throws Exception {
        while (true) {
            Jedis jedis = cn.itheima.spider.version2.utils.JedisUtils.getJedis();
            List<String> list = jedis.brpop(20, "bigData:spider:news163:docurl");
            jedis.close();
            if (list == null || list.size() == 0) {
                break;
            }
            //获取到了url
            News news = parseNews163Html(list.get(1));

            //将news对象转换为json字符串,保存到redis中
            Gson gson = new Gson();
            String newsJson = gson.toJson(news);
            System.out.println(newsJson);
            jedis = JedisUtils.getJedis();
            jedis.lpush("bigData:spider:newsJson", newsJson);
            jedis.close();
        }
    }

    /**
     * 解析新闻详情页的数据
     *
     * @param docurl
     * @return
     */
    private static News parseNews163Html(String docurl) throws Exception {
        IdWorker idWorker = new IdWorker(0, 0);
        //发送请求，获取数据
        String html = HttpClientUtils.doGet(docurl);
        News news = new News();
        news.setId(idWorker.nextId());
        //获取document对象
        Document document = Jsoup.parse(html);
        //解析数据
        Elements titleEl = document.select("#epContentLeft>h1");
        news.setTitle(titleEl.text());
        news.setDocurl(docurl);
        //时间来源
        Elements timeAndSource = document.select(".post_time_source");
        String timeAndSourceText = timeAndSource.text();
        String[] timeAndSourceArr = timeAndSourceText.split(" ");
        if (timeAndSourceArr.length == 2) {
            news.setTime(timeAndSourceArr[0]);
            news.setSource(timeAndSourceArr[1]);
        }
        //内容
        Elements contentEl = document.select("#endText>p");
        news.setContent(contentEl.text());
        //编辑
        Elements editorEl = document.select(".ep-editor");
        String editor = editorEl.text();
        editor = editor.substring(editor.indexOf(": ") + 1, editor.lastIndexOf("_"));
        news.setEditor(editor);
        return news;

    }
}
