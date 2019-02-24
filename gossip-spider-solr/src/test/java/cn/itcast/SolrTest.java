package cn.itcast;

import cn.itcast.pojo.Product;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SolrTest {
    //使用solr完成写入索引的操作
    @Test
    public void indexWriterTest01() throws IOException, SolrServerException {
        //1.创建solrj连接solr服务器的服务对象
        //如果想要李恩杰solr进行写入或者查询的时候，需要使用http请求
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //2.添加原始文档数据
        SolrInputDocument doc = new SolrInputDocument();
        //添加的字段必须是solr配置文件中已经存在的默认字段，如果要添加不存在的，使用动态域进行添加
        doc.addField("id", "5");
        doc.addField("title", "Lucene");
        doc.addField("content", "lucene是开源搜索LUCENE引擎底层架构");
        solrServer.add(doc);

        //3.提交
        solrServer.commit();
    }

    //如何添加多条数据
    @Test
    public void indexWriterTest02() throws IOException, SolrServerException {
        //创建slorj连接solr服务器的服务对象
        //如果想要连接solr进行写入或者查询的时候，需要使用http请求
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");
        //添加原始文档数据
        List<SolrInputDocument> docs = new ArrayList<>();

        SolrInputDocument doc = new SolrInputDocument();
        //添加solr字段必须是solr配置文件中已经存在的默认字段，如多要添加不存在的，使用动态域进行添加
        doc.addField("id", "2");
        doc.addField("title", "双十二马上到了");
        doc.addField("content", "钱包准备好了");
        docs.add(doc);

        SolrInputDocument doc2 = new SolrInputDocument();
        doc2.addField("id", "3");
        doc2.addField("title", "圣诞节也要到了");
        doc2.addField("content", "女朋友准备好了");
        docs.add(doc2);

        solrServer.add(docs);

        //提交
        solrServer.commit();
    }

    //使用javaBean完成索引写入的操作
    @Test
    public void indexWriterTest03() throws IOException, SolrServerException {
        //创建solrj连接solr的服务对象
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //添加原始数据
        Product product = new Product("4", "iPhone x max", "最新款苹果", "很贵");
        solrServer.addBean(product);

        //提交
        solrServer.commit();
    }

    //索引删除
    @Test
    public void delIndexTest04() throws IOException, SolrServerException {
        //创建solr连接solr的服务对象
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //添加删除的条件内容
        solrServer.deleteById("4");
        // 删除所有
//        solrServer.deleteByQuery("*:*");

        solrServer.commit();
    }


}
