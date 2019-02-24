package cn.itcast;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;

public class indexWriterTest {
    //solrj写入索引
    @Test
    public void indexWriterTest01() throws Exception {
        //1.创建solr集群的服务对象
        //指定zookeeper的地址
        String zkHost = "192.168.78.141:2181,192.168.78.142:2181,192.168.78.143:2181";
        CloudSolrServer solrServer = new CloudSolrServer(zkHost);

        solrServer.setDefaultCollection("collection2");//默认连接那个索引库(大)
        //从zookeeper中获取solr节点的连接对象，超时时间
        //zookeeper帮我们找到一台不是很繁忙的节点，供我们使用
        solrServer.setZkConnectTimeout(5000);
        //与zookeeper获取连接的时候，超时时间
        solrServer.setZkClientTimeout(5000);

        solrServer.connect();//获取solr连接

        //2.添加索引
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("id", "1");
        doc.addField("title", "一起来抢红包吧");
        doc.addField("content", "现在在上solrCloud，不能抢");

        solrServer.add(doc);

        //3.提交数据
        solrServer.commit();
    }

    //索引的删除
    @Test
    public void delIndex() throws IOException, SolrServerException {
        //1.创建solr集群的服务对象
        String zkHost = "192.168.78.141:2181,192.168.78.142:2181,192.168.78.143:2181";
        CloudSolrServer solrServer = new CloudSolrServer(zkHost);

        solrServer.setDefaultCollection("collection2");//指定要连接的索引库
        solrServer.setZkClientTimeout(5000);
        solrServer.setZkConnectTimeout(5000);

        solrServer.connect();//获取连接

        //2.执行删除操作
        solrServer.deleteById("1");

        //3.提交数据
        solrServer.commit();
    }

    //查询索引
    @Test
    public void selectIndex() throws SolrServerException {
        //1.创建solr集群的服务对象
        String zkHost = "192.168.78.141:2181,192.168.78.142:2181,192.168.78.143:2181";
        CloudSolrServer solrServer = new CloudSolrServer(zkHost);

        solrServer.setDefaultCollection("collection2");
        solrServer.setZkClientTimeout(5000);
        solrServer.setZkConnectTimeout(5000);

        solrServer.connect();

        //2.执行查询操作
        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrServer.query(solrQuery);

        //3.获取数据
        SolrDocumentList documentList = response.getResults();

        for (SolrDocument document : documentList) {
            String id = (String) document.get("id");
            String title = (String) document.get("title");
            String content = (String) document.get("content");

            System.out.println("id:" + id + " title: " + title + " content: " + content);
        }
    }
}
