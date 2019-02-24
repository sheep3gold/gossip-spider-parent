package cn.itcast;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class demoTest {
    @Test
    public void indexSearch() throws Exception {
        //创建查询的核心对象
        FSDirectory d = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        DirectoryReader reader = DirectoryReader.open(d);
        IndexSearcher indexSearcher = new IndexSearcher(reader);
        //执行查询
        QueryParser queryParser = new QueryParser("id", new IKAnalyzer());
        Query query = queryParser.parse("10");
        TopDocs topDocs = indexSearcher.search(query, 10);

        //获取文档id
        //获取得分文档集合
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int id = scoreDoc.doc;//文档id
            float score = scoreDoc.score;//文档得分
            Document doc = indexSearcher.doc(id);
            String docId = doc.get("id");
            String title = doc.get("title");
            System.out.println(docId + "  " + title + "  " + "得分为:" + score);
        }
    }

    //提取一个查询的方法
    public void query(Query query) throws IOException {
        //创建查询的核心对象
        IndexReader reader = DirectoryReader.open(FSDirectory.open(new File("/Users/yangxin/test/lucene")));
        IndexSearcher indexSearcher = new IndexSearcher(reader);

        //执行查询
        TopDocs topDocs = indexSearcher.search(query, 20);

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            int docId = scoreDoc.doc;
            float score = scoreDoc.score;
            Document doc = indexSearcher.doc(docId);
            String content = doc.get("content");
            String title = doc.get("title");
            String id = doc.get("id");
            System.out.println("文档得分为  " + score + "  " + id + "  " + title);
        }
    }

    //词条查询
    @Test
    public void termQuery() throws IOException {
        //创建词条对象
        //注意：词条是不可再分割的，词条可以是一个字，也可以是一句话
        //使用场景：主要是针对的是不可再分割的字段，例如id
        //由于其不可再分，可以搜索 全文 ，但是不能搜索 全文检索
        TermQuery termQuery = new TermQuery(new Term("content", "谷歌"));
        query(termQuery);
    }

    //通配符查询
    @Test
    public void wildcardQuery() throws IOException {
        //通配符：
        //*：代表多个字符
        //?:代表一个字符
        WildcardQuery wildcardQuery = new WildcardQuery(new Term("title", "*"));
        query(wildcardQuery);
    }

    //模糊查询 fuzzQuery
    @Test
    public void fuzzQuery() throws IOException {
        /**
         * 模糊查询:
         *       指的是通过替换, 补位, 移动 能够在二次切换内查询数据即可返回
         *       参数1: term  指定查询的字段和内容
         *       参数2: int n   表示最大编辑的次数  最大2
         */
        FuzzyQuery fuzzyQuery = new FuzzyQuery(new Term("title", "谷歌"), 1);
        query(fuzzyQuery);
    }

    //数值范围查询
    @Test
    public void numbericRangeQuery() throws IOException {
        /**
         * 获取NumericRangeQuery的方式:
         * 通过提供的静态方法获取:
         *     NumericRangeQuery.newIntRange()
         *     NumericRangeQuery.newFloatRange()
         *     NumericRangeQuery.newDoubleRange()
         *     NumericRangeQuery.newLongRange()
         *
         *
         * 数值范围查询:
         *     参数1: 指定要查询的字段
         *     参数2: 指定要查询的开始值
         *     参数3: 指定要查询的结束值
         *     参数4: 是否包含开始
         *     参数5: 是否包含结束
         */
        NumericRangeQuery numericRangeQuery = NumericRangeQuery.newIntRange("id", 0, 20, false, false);
        query(numericRangeQuery);
    }

    //组合查询
    @Test
    public void testBooleanQuery() throws IOException {
        NumericRangeQuery query1 = NumericRangeQuery.newIntRange("id", 2, 15, true, true);
        NumericRangeQuery query2 = NumericRangeQuery.newIntRange("id", 0, 15, true, true);

        //boolean查询本身并没有查询条件，它可以组合其他查询
        BooleanQuery query = new BooleanQuery();
        //交集：Occur.MUST+Occur.Must
        //并集：Occur.SHOULD+Occur.SHOULD
        //非:Occur.MUST_OUT
        query.add(query1, BooleanClause.Occur.SHOULD);
        query.add(query2, BooleanClause.Occur.SHOULD);

        query(query);
    }
}
