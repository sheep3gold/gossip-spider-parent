package cn.itcast;

import cn.itcast.pojo.Product;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;

import java.util.List;

public class SolrSearchTeat {
    //查询
    @Test
    public void indexSearcherTest01() throws SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //执行查询
        //SolrQuery:查询条件的集合
        SolrQuery solrQuery = new SolrQuery("*:*");//查询所有数据
        QueryResponse response = solrServer.query(solrQuery);

        //获取数据
        SolrDocumentList documentList = response.getResults();

        for (SolrDocument document : documentList) {
            String id = (String) document.get("id");
            String title = (String) document.get("title");
            String content = (String) document.get("content");

            System.out.println("文档的id:" + id + "文档的title:" +title+ "文档的content:" +content);

        }
    }

    //查询索引，返回javaBean的方式
    @Test
    public void indexSearcherTest02() throws Exception {
        //创建solr连接solr服务对象
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //执行查询
        SolrQuery solrQuery = new SolrQuery("*:*");
        QueryResponse response = solrServer.query(solrQuery);

        //获取数据
        List<Product> productList = response.getBeans(Product.class);

        for (Product product : productList) {
            System.out.println(product);
        }
    }

    //复杂查询
    @Test
    public void queryTest() throws SolrServerException {
        HttpSolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //1.通配符查询
//        SolrQuery solrQuery = new SolrQuery("lu?en?");

        //2.相识度查询：类似lucene的当中模糊查询
        //在solr中，最大的编辑次数也是0-2的范围，如果书写的不是这个范围的数，那么就采用默认的值2
//        SolrQuery solrQuery = new SolrQuery("luce~2");

        // 3.范围查询:solr支持 文本，日期，数字
        //关键字大写
        //格式：[start TO end]
        // 注意：id 范围查询这个是文本范围
        //字典排序方式进行范围查询
        //例如：1，2，3，，20，22，33，34，15：[1 TO 3] 结果：1，15，2，20，22，3，33，34
        //日期格式：solr规定日期的格式必须是UTC格式的：yyyy-MM-dd'T'HH:mm:ss'z'
        //solr采用的国际标准时间，国际标准时间和中国的时间是有时差的+8小时
        //new Date();//-8小时
        //在进行日期添加的时候，建议不要使用date对象，使用字符串的方式来添加日期（格式正确）
//        SolrQuery solrQuery = new SolrQuery("id:[1 TO 3]");

        //4.组合查询：将多个条件组合在一起
        //AND:与lucene的MUST 必须
        //NOT: 与lucene的MUST_NOT
        //OR:与lucene的SHOULD
//        SolrQuery solrQuery = new SolrQuery("lucene OR id:2");

        //5.子查询
        SolrQuery solrQuery = new SolrQuery("(lucene OR id:2)");

        QueryResponse response = solrServer.query(solrQuery);


        SolrDocumentList results = response.getResults();
        for (SolrDocument result : results) {
            String id = (String) result.get("id");
            String content = (String) result.get("content");
            System.out.println("id:" + id + " " + "content:" + content);
        }
    }
}
