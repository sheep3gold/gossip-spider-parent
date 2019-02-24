package cn.itheima.spider.version2.gossip;

import cn.itheima.spider.version2.pojo.News;
import cn.itheima.spider.version2.utils.HttpClientUtils;
import cn.itheima.spider.version2.utils.JedisUtils;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Map;

/**
 * 根据url,获取新闻详情页的url，将url保存到redis中，保存之前，判断是否已经爬取
 */
public class News163Master {
    public static void main(String[] args) throws Exception {
        page();
    }

    //获取下一页的url
    public static void page() throws Exception {
        String indexUrl = "http://ent.163.com/special/000380VU/newsdata_index.js?callback=data_callback";
        int page = 2;
        while (true) {
            //发送请求，获取资源
            String newsJson = HttpClientUtils.doGet(indexUrl);
            if (newsJson == null) {
                break;
            }
            //解析新闻数据：json数据
            parseNewsJson(newsJson);

            //获取下一页的url
            if (page < 10) {
                indexUrl = "http://ent.163.com/special/000380VU/newsdata_index_0"
                        + page + ".js?callback=data_callback";
                page++;
            }else {
                indexUrl = "http://ent.163.com/special/000380VU/newsdata_index_"
                        + page + ".js?callback=data_callback";
                page++;
            }
        }
    }

    /**
     * 解析新闻Json数据
     *
     * @param newsJson
     */
    private static void parseNewsJson(String newsJson) {
        if (newsJson.indexOf("(") > 0 && newsJson.indexOf("(") < newsJson.indexOf("{")) {
            newsJson = parseJson(newsJson);
        }

        //将json转换为集合
        Gson gson = new Gson();
        List<Map<String, String>> list = gson.fromJson(newsJson, List.class);

        for (Map<String, String> map : list) {
            if ("图集".equals(map.get("label"))) {
                continue;
            }
            if (!map.get("docurl").contains("ent.163.com")) {
                continue;
            }
            News news163 = new News();
            //获取新闻详情页的url
            String docurl = map.get("docurl");
            if (docurl == null) {
                continue;
            }
            if (!docurl.contains("photoview")) {
                //判断是否已经爬取过
                Jedis jedis = JedisUtils.getJedis();
                Boolean flag = jedis.sismember("bigData:spider:newsSpider:docurl", docurl);
                jedis.close();
                if (flag) {
                    continue;
                }
                //获取到了未爬取过的url
                jedis = JedisUtils.getJedis();
                jedis.lpush("bigData:spider:news163:docurl", docurl);
                jedis.close();
            }
        }
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
