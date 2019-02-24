package cn.itheima.spider.version2.gossip;

import cn.itheima.spider.version2.dao.NewsDao;
import cn.itheima.spider.version2.pojo.News;
import cn.itheima.spider.version2.producer.KafkaSpiderProducer;
import cn.itheima.spider.version2.utils.JedisUtils;
import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * 从redis中获取newsJson，将json数据转换为news对象,将news对象保存到数据库，保存成功以后，
 * 将news中的url保存到redis中
 */
public class PublicDaoNode {
    private static NewsDao dao = new NewsDao();

    //创建生产者对象
    private static KafkaSpiderProducer kafkaSpiderProducer = new KafkaSpiderProducer();

    public static void main(String[] args) {
        while (true) {
            //从redis中获取newsJson
            Jedis jedis = JedisUtils.getJedis();
            List<String> list = jedis.brpop(20, "bigData:spider:newsJson");
            jedis.close();
            if (list == null || list.size() == 0) {
                break;
            }
            //获取到了newsJson
            Gson gson = new Gson();
            News news = gson.fromJson(list.get(1), News.class);

            dao.addNews(news);

            System.out.println(list.get(1));
            //将newsJson写入kafka
            kafkaSpiderProducer.messageToKafka(list.get(1));

            //将保存成功的news对象的url添加到redis中,防止出现重复爬取
            jedis = JedisUtils.getJedis();
            jedis.sadd("bigData:spider:newsSpider:docurl", news.getDocurl());
            jedis.close();
        }
    }
}
