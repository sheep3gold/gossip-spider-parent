package cn.itcast;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class HighLightTest {
    /**
     * Lucene 的高亮实现
     * @throws IOException
     * @throws ParseException
     * @throws InvalidTokenOffsetsException
     */
    @Test
    public void testHighlighter() throws Exception {
        //目录对象
        FSDirectory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        //创建读取工具
        IndexReader reader = DirectoryReader.open(directory);
        //创建搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("title", new IKAnalyzer());
        Query query = parser.parse("谷歌地图");

        //格式化器
        SimpleHTMLFormatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
        QueryScorer scorer = new QueryScorer(query);
        //准备高亮工具
        Highlighter highlighter = new Highlighter(formatter, scorer);
        //搜索
        TopDocs topDocs = searcher.search(query, 20);
        System.out.println("本次搜索共" + topDocs.totalHits + "条数据");

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //获取文档编号
            int docID = scoreDoc.doc;
            Document doc = reader.document(docID);
            System.out.println("id:" + doc.get("id"));

            String title = doc.get("title");
            //用高亮工具处理普通查询结果，参数：分词器，要高亮的字段的名称，高亮字段的原始值
            String hTitle = highlighter.getBestFragment(new IKAnalyzer(), "title", title);
            System.out.println("title:" + hTitle);
            //获取文档的得分
            System.out.println("得分："+scoreDoc.score);
        }
    }

    /**
     * 排序
     * @throws Exception
     */
    @Test
    public void testSortQuery() throws Exception {
        //目录对象
        FSDirectory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        //创建读取工具
        DirectoryReader reader = DirectoryReader.open(directory);
        //创建搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("content", new IKAnalyzer());
        Query query = parser.parse("学习");

        //创建排序对象，需要排序字段SortedField，参数：字段的名称、字段的类型、
        //是否反转如果是false,升序，true降序
        Sort sort = new Sort(new SortField("id", SortField.Type.LONG, true));
        //搜索
        TopDocs topDocs = searcher.search(query, 10, sort);
        System.out.println("本次搜索共：" + topDocs.totalHits + "条数据");

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            //获取文档编号
            int docID = scoreDoc.doc;
            Document doc = reader.document(docID);
            System.out.println("id:" + doc.get("id"));
            System.out.println("content:" + doc.get("content"));
        }
    }

    /**
     * Lucene的分页
     * @throws Exception
     */
    @Test
    public void testPageQuery() throws Exception {
        //实际上Lucene本身不支持分页，因此我们需要自己进行逻辑分页
        int pageSize = 2;//每页条数
        int pageNum = 3;//当前页码
        int start = (pageNum - 1) * pageSize;//当前页的起始条数
        int end = start + pageSize;//当前页的结束条数（不能包含）

        //目录对象
        FSDirectory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        //创建读取工具
        DirectoryReader reader = DirectoryReader.open(directory);
        //创建搜索工具
        IndexSearcher searcher = new IndexSearcher(reader);

        QueryParser parser = new QueryParser("title", new IKAnalyzer());
        Query query = parser.parse("谷歌地图");

        //创建排序对象，需要排序字段SortedField,参数：字段的名称、字段的类型、是否反转，
        //false升序，true降序
        Sort sort = new Sort(new SortField("id", SortField.Type.LONG, false));
        //搜索数据,查询0-end条
        TopFieldDocs topDocs = searcher.search(query, end, sort);//???????????????
        System.out.println("本次搜索共" + topDocs.totalHits + "条数据");

        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (int i = start; i < end; i++) {
            ScoreDoc scoreDoc = scoreDocs[i];
            //获取文档编号
            int docID = scoreDoc.doc;
            Document doc = reader.document(docID);
            System.out.println("id:" + doc.get("id"));
            System.out.println("title" + doc.get("title"));
        }

    }

}
