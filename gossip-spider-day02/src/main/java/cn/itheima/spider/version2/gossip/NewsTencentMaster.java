package cn.itheima.spider.version2.gossip;

import cn.itheima.spider.version2.pojo.News;
import cn.itheima.spider.version2.utils.HttpClientUtils;
import cn.itheima.spider.version2.utils.IdWorker;
import cn.itheima.spider.version2.utils.JedisUtils;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 获取腾讯新闻的热点和非热点新闻，将对象news转换为json数据，保存在redis中
 *
 */
public class NewsTencentMaster {
    public static void main(String[] args) throws Exception {
        String topUrl =
                "https://pacaio.match.qq.com/irs/rcd?cid=137&token=d0f13d594edfc180f5bf6b845456f3ea&id=&ext=ent&num=60&expIds=&callback=__jp0";
        String noTopUrl =
                "https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=ent&page=0";
        page(topUrl, noTopUrl);
    }

    private static void page(String topUrl, String noTopUrl) throws Exception {
        //处理热点新闻数据
        String topJson = HttpClientUtils.doGet(topUrl);
        List<News> topNewsList = parseNewsJson(topJson);
        if (topNewsList != null && topNewsList.size() > 0) {
            addNews(topNewsList);
        }
        //非热点新闻
        Integer page = 1;
        while (true) {
            page++;
            //处理非热点新闻
            String noTopJson = HttpClientUtils.doGet(noTopUrl);
//            System.out.println(page);
            List<News> noTopNewsList = parseNewsJson(noTopJson);
            if (noTopNewsList == null || noTopNewsList.size() == 0) {
                break;
            }
            //保存数据
            addNews(noTopNewsList);
            //分页
            noTopUrl = "https://pacaio.match.qq.com/irs/rcd?cid=146&token=49cbb2154853ef1a74ff4e53723372ce&ext=ent&page=" + page;

        }
    }

    private static void addNews(List<News> list) {
        Gson gson = new Gson();
        for (News news : list) {
            String newsJson = gson.toJson(news);
            System.out.println(newsJson);
            Jedis jedis = JedisUtils.getJedis();
            jedis.lpush("bigData:spider:newsJson", newsJson);
            jedis.close();
        }
    }

    private static List<News> parseNewsJson(String newsJson) throws Exception {
        IdWorker idWorker = new IdWorker(1, 0);
        if (newsJson == null) {
            return null;
        }
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
//        System.out.println(datanum);

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
                    Boolean flag = jedis.sismember("bigData:spider:newsSpider:docurl", (String) dataMap.get("url"));
                    jedis.close();
                    if (flag) {
                        continue;
                    }
                    //解析新闻数据
                    News tencentNews = new News();
                    tencentNews.setId(idWorker.nextId());
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
     * 去除json中'(' ')'
     * @param newsJson
     * @return
     */
    private static String parseJson(String newsJson) {
        int first = newsJson.indexOf("(");
        int last = newsJson.lastIndexOf(")");
        newsJson = newsJson.substring(first + 1, last);
        return newsJson;
    }
}
