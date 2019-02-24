package cn.itcast.search;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.List;
import java.util.Map;

public class IndexSearcherTest {
    public static void main(String[] args) throws Exception {
        Integer page = 1;
        Integer pageSize = 3;

        //1.创建solrj连接solr的服务对象
        SolrServer solrServer = new HttpSolrServer("http://localhost:8080/solr/collection1");

        //2.执行查询
//        SolrQuery solrQuery = new SolrQuery("title:Lucene");
        SolrQuery solrQuery = new SolrQuery("title:*");
        //排序
        solrQuery.setSort("id", SolrQuery.ORDER.asc);

        //分页:mysql中limit起始值，每页条数
        solrQuery.setStart((page - 1) * pageSize);
        solrQuery.setRows(pageSize);

        //高亮
        solrQuery.setHighlight(true);
        solrQuery.addHighlightField("title");//solr支持对多个字段设置高亮
        solrQuery.addHighlightField("content");
        solrQuery.setHighlightSimplePre("<font color='red'>");
        solrQuery.setHighlightSimplePost("</font>");

        System.out.println(solrQuery);
        QueryResponse response = solrServer.query(solrQuery);

        //获取数据
        //获取高亮的内容
        //最外层的map:key 其实是文档的id value:高亮的内容(map)
        //内层的map:key 其实就是高亮的字段 value:高亮内存(集合)
        //list集合中就是高亮的内容，集合中数据一般只有一个
        SolrDocumentList documents = response.getResults();
        System.out.println("共查询到记录：" + documents.getNumFound());

        Map<String, Map<String, List<String>>> map = response.getHighlighting();
        System.out.println("map中的内容：" + map);

        for (SolrDocument doc : documents) {
            System.out.println(doc.get("id"));
            List<String> hightDocs = map.get(doc.get("id")).get("title");
            List<String> highDocsContent = map.get(doc.get("id")).get("content");
            if (hightDocs != null) {
                System.out.println("高亮显示的商品名称：" + hightDocs.get(0) + " 内容：" + highDocsContent);
            }else
                System.out.println(doc.get("title"));
//            System.out.println(hightDocs + "  " + highDocsContent);
        }

        /*for (String key : map.keySet()) {
            Map<String, List<String>> listMap = map.get(key);
            for (String s : listMap.keySet()) {
                List<String> list = listMap.get(s);
                System.out.println(list);
            }
        }*/
//        System.out.println(map);
//        List<Product> productList = response.getBeans(Product.class);
//        //docs中数据
//        for (Product product : productList) {
////            Map<String, List<String>> listMap = map.get(product.getId());
////            List<String> list = listMap.get("title");
////            if (list != null && list.size() != 0) {
////                String title = list.get(1);
////                product.setTitle(title);
////            }
////            List<String> list1 = listMap.get("content");
////            if (list1 != null && list1.size() != 0) {
////                String content = list1.get(1);
////                product.setContent(content);
////            }
//            System.out.println(product);
//        }
    }
}
