package cn.itcast.gossip;

import cn.itcast.dao.NewsDao;
import cn.itcast.pojo.News;
import cn.itcast.utils.HttpClientUtils;
import cn.itcast.utils.JedisUtils;
import com.google.gson.Gson;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 需求: 爬取163 与腾讯新闻的 娱乐新闻数据
 **/
public class GossipSpider {
    private static NewsDao newsDao = new NewsDao();

    public static void main(String[] args) throws Exception {
        String news163IndexUrl =
                "http://ent.163.com/special/000380VU/newsdata_index.js?callback=data_callback";
//        String news = HttpClientUtils.doGet(news163IndexUrl);
        page163(news163IndexUrl);
    }

    /**
     * 解析新闻的json数据
     * @param news
     */
    private static void parseNewsJson(String news) throws Exception {
        int first = news.indexOf("(");
        int last = news.lastIndexOf(")");
        //获取json数据
        String news163Json = news.substring(first + 1, last);

        //解析数据
        //将json转换为集合
        Gson gson = new Gson();
        List<Map<String, String>> list = gson.fromJson(news163Json, List.class);
//        System.out.println(list.get(0));
        for (Map<String, String> map : list) {
            if ("图集".equals(map.get("label"))) {
                continue;
            }
            News news163 = new News();
            //获取新闻详情页的url
            String docurl = map.get("docurl");
            if (docurl == null) {
                continue;
            }
            if (!docurl.contains("photoview")) {
                //判断是否爬取过
                Jedis jedis = JedisUtils.getJedis();
                Boolean flag = jedis.sismember("bigData:spider:news163:docurl", docurl);
                jedis.close();
                if (flag) {
                    continue;
                }
                //解析新闻的详情页内容
                news163 = parse163News(docurl);
                //保存数据
                newsDao.addNews(news163);
                //成功保存了数据，将这个url保存到redis中
                jedis = JedisUtils.getJedis();
                jedis.sadd("bigData:spider:news163:docurl", docurl);
                jedis.close();
            }

        }
//        String docurl = list.get(0).get("docurl");
//        News news1 = parse163News(docurl);
    }

    /**
     * 解析新闻详情页的json数据，将json转换为news对象
     * @param docUrl
     * @return
     * @throws Exception
     */
    public static News parse163News(String docUrl) throws Exception {
        String html = HttpClientUtils.doGet(docUrl);

        //解析数据
        Document document = Jsoup.parse(html);

        //解析新闻的时间和来源
        News news = new News();
        Elements timeAndSourceEl = document.select(".post_time_source");
        if (timeAndSourceEl.size() > 0) {
            String timeAndSource = timeAndSourceEl.text();
//            System.out.println(timeAndSource);
            String[] split = timeAndSource.split("　");
            System.out.println(Arrays.toString(split));
            //时间
            news.setTime(split[0]);
//            System.out.println(split[0]);
            news.setSource(split[1]);
        }
        //解析新闻的正文
        Elements pEl = document.select("#endText>p");
        if (pEl.size() > 0) {
            news.setContent(pEl.text());
        }
        //获取新闻的编辑
        Elements editorEl = document.select(".ep-editor");
        if (editorEl.size()>0) news.setEditor(editorEl.text());
        //获取新闻的标题
        Elements titleEl = document.select("#epContentLeft>h1");
        if (titleEl.size()>0) news.setTitle(titleEl.text());
        //获取新闻的url
        news.setDocurl(docUrl);

        return news;
    }

    /**
     * 分页处理
     * @param news163IndexUrl
     * @throws Exception
     */
    public static void page163(String news163IndexUrl) throws Exception {
        String page = "02";
        while (true) {
            String news = HttpClientUtils.doGet(news163IndexUrl);
            if (news != null) {
                parseNewsJson(news);

                //进行url拼接
                news163IndexUrl = "http://ent.163.com/special/000380VU/newsdata_index_"
                        + page + ".js?callback=data_callback";

                int i = Integer.parseInt(page);
                i = i + 1;
                if (i < 10) {
                    page = "0" + i;
                }else {
                    page = i + "";
                }
                System.out.println(news163IndexUrl);
                news = HttpClientUtils.doGet(news163IndexUrl);
            }else {
                break;
            }
        }
    }


}
