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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 需求: 爬取163 与腾讯新闻的 娱乐新闻数据
 **/
public class GossipSpider1 {
    private static NewsDao newsDao = new NewsDao();

    public static void main(String[] args) throws Exception {
        String topUrl =
                "https://pacaio.match.qq.com/irs/rcd?cid=137&token=d0f13d594edfc180f5bf6b845456f3ea&id=&ext=ent&num=60&expIds=&callback=__jp0";
        String noTopUrl =
                "https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=ent&page=0";
        page(topUrl, noTopUrl);
    }
    /**
     * 分页处理
     * @param topUrl
     * @param noTopUrl
     * @throws Exception
     */
    public static void page(String topUrl, String noTopUrl) throws Exception {
        //处理热点新闻数据
        String topJson = HttpClientUtils.doGet(topUrl);
        List<News> topNewsList = parseNewsJson(topJson);
        if (topNewsList != null && topNewsList.size() > 0) {
            addNews(topNewsList);
        }
        //非热点新闻
        Integer page = 1;
        while (true) {
            //处理非热点新闻数据
            String noTopJson = HttpClientUtils.doGet(noTopUrl);
            List<News> noTopNewsList = parseNewsJson(noTopJson);
            if (noTopNewsList == null || noTopNewsList.size() == 0) {
                break;
            }
            //保存数据
            addNews(noTopNewsList);
            //执行分页
            noTopUrl = "https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=ent&page="
                    + page;
            page++;
        }
    }

    /**
     * 解析新闻的json数据
     *
     * @param newsJson
     */
    private static List<News> parseNewsJson(String newsJson) throws Exception {
        if (newsJson.indexOf("(") > 0 && newsJson.indexOf("(") < newsJson.indexOf("{")) {
            newsJson = parseJson(newsJson);
        }
        //解析数据
        //将json转换为集合
        Gson gson = new Gson();
        Map<String, Object> newsMap = gson.fromJson(newsJson, Map.class);
        //判断获取了多少条新闻
        Double datanum = (Double) newsMap.get("datanum");
        int num = datanum.intValue();
        if (num == 0) {
            return null;
        }
        System.out.println(datanum);

        List<News> tencentNewsList = new ArrayList<>();
        for (String key : newsMap.keySet()) {
            if ("data".equals(key)) {
                List<Map<String, Object>> o = (List<Map<String, Object>>) newsMap.get(key);
                if (o == null || o.size() == 0) {
                    return null;
                }
                for (Map<String, Object> dataMap : o) {
                    //判断是否爬取过
                    Jedis jedis = JedisUtils.getJedis();
                    Boolean flag = jedis.sismember("bigData:spider:tencent:docurl", (String) dataMap.get("url"));
                    jedis.close();
                    if (flag) {
                        continue;
                    }
                    //解析新闻数据
                    News tencentNews = new News();
                    //进行数据封装
                    for (String newsKey : dataMap.keySet()) {
                        if ("title".equals(newsKey)) {
                            tencentNews.setTitle((String) dataMap.get(newsKey));
                        }
                        if ("url".equals(newsKey)) {
                            tencentNews.setDocurl((String) dataMap.get(newsKey));
                        }
                        if ("update_time".equals(newsKey)) {
                            tencentNews.setTime((String) dataMap.get(newsKey));
                        }
                        if ("source".equals(newsKey)) {
                            tencentNews.setSource((String) dataMap.get(newsKey));
                            tencentNews.setEditor((String) dataMap.get(newsKey));
                        }
                        if ("intro".equals(newsKey)) {
                            tencentNews.setContent((String) dataMap.get(newsKey));
                        }
                    }
                    //添加到集合中
                    tencentNewsList.add(tencentNews);
                }
            }
        }
        return tencentNewsList;
    }

    /**
     * 去除括号
     * @param noJson
     * @return
     */
    public static String parseJson(String noJson) {
        int first = noJson.indexOf("(");
        int last = noJson.lastIndexOf(")");

        noJson = noJson.substring(first + 1, last);
        return noJson;
    }

    /**
     * 保存数据
     * @param list
     */
    public static void addNews(List<News> list) {
        for (News news : list) {
            Jedis jedis = JedisUtils.getJedis();
            Boolean flag = jedis.sismember("bigData:spider:tencent:docurl", news.getDocurl());
            if (flag) {
                continue;
            }
            jedis.close();

            newsDao.addNews(news);

            jedis = JedisUtils.getJedis();
            jedis.sadd("bigData:spider:tencent:docurl", news.getDocurl());
            jedis.close();
        }
    }







}
