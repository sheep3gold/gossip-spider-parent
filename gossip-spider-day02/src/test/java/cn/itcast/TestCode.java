package cn.itcast;


import cn.itheima.spider.version2.utils.HttpClientUtils;
import cn.itheima.spider.version2.utils.JedisUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.Test;
import redis.clients.jedis.Jedis;

import java.io.*;

public class TestCode {
    @Test
    public void fullHttpGet() throws Exception {
        int i = 0;
//        while (i<100) {
        HttpGet httpGet = new HttpGet("https://listmyblog.cn");
        HttpGet httpGet1 = new HttpGet("https://busuanzi.ibruce.info/busuanzi/2.3/busuanzi.pure.mini.js");
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.9");
        httpGet.setHeader("Cache-Control", "max-age=0");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Cookie", "P_INFO=m15927252038@163.com|1541055679|0|mail163|00&99|shh&1534470865&mail163#jis&320100" +
                "#10#0#0|159038&0||15927252038@163.com; nts_mail_user=m15927252038@163.com:-1:1; _ntes_nnid=653a618d26078c3785adc1884ad19b57," +
                "1541055686986; _ntes_nuid=653a618d26078c3785adc1884ad19b57; UM_distinctid=16780e62a42722-0f65aec9401704-35677607-1aeaa0-16780e62a4311db;" +
                " vjuids=113e3dc9e4.16780e62d32.0.f324086218e0e; __gads=ID=d3c0950b3656e491:T=1544055829:S=ALNI_MZLoMS3np6BaLJOWjbcSn8DE77xSQ; " +
                "NNSSPID=6412d7cf5cde4721afa64487c1e90364; vjlast=1544055828.1547782975.12; _antanalysis_s_id=1547782975573; ne_analysis_trace_id=1547872538328; " +
                "s_n_f_l_n3=eb8448f1f3848c3d1547872538330; vinfo_n_f_l_n3=eb8448f1f3848c3d.1.8.1544055827798.1547783264613.1547872565129");
        httpGet.setHeader("Host", "news.163.com");
        httpGet.setHeader("If-Modified-Since", "Sat, 19 Jan 2019 04:32:01 GMT");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_2) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/71.0.3578.98 Safari/537.36");

        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse res = httpClient.execute(httpGet);
        HttpEntity entity = res.getEntity();
        String html = EntityUtils.toString(entity, "utf-8");
//        System.out.println(html);
        CloseableHttpResponse res1 = httpClient.execute(httpGet1);
        HttpEntity entity1 = res1.getEntity();
        String html1 = EntityUtils.toString(entity1, "utf-8");
        System.out.println(html1);
        Document document = Jsoup.parse(html);
        Elements numEl = document.select("#busuanzi_value_site_uv");
        String num = numEl.text();
        i++;
        System.out.println(i + ":  " + num);
//        }
    }

    @Test
    public void testInitIp() throws IOException {
        Jedis jedis = JedisUtils.getJedis();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(
                new File("/Users/yangxin/Desktop/Proxies2019-01-19.txt")));
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            jedis.lpush("spider:ip", line);
        }
        bufferedReader.close();
        jedis.close();
    }

    @Test
    public void testDynamicIp() throws Exception {
        HttpClientUtils.setDynamicIpToRedis();
    }
}
