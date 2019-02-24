package cn.itcast;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.NumericRangeQuery;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

public class UpdateTest {
    /**
     *
     */
    @Test
    public void testUpdate() throws IOException {
        // 创建文档对象
        Document document = new Document();
        document.add(new StringField("id", "9", Field.Store.YES));
        document.add(new TextField("title", "谷歌地图之父跳槽FaceBook", Field.Store.YES));

        // 索引库对象
        Directory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        // 索引写入器配置对象
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        // 索引写入器对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        // 执行更新操作
        //id必须是String类型
        indexWriter.updateDocument(new Term("id", "5"), document);
        // 提交
        indexWriter.commit();
        // 关闭
        indexWriter.close();
    }

    //索引删除
    @Test
    public void testDelete() throws IOException {
        //创建目录对象
        FSDirectory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        //创建索引写入器配置对象
        IndexWriterConfig conf = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        //创建索引写入器对象
        IndexWriter indexWriter = new IndexWriter(directory, conf);

        //执行删除操作(根据词条),要求id字段必须是字符串类型
//        indexWriter.deleteDocuments(new Term("id", "9"));
        //根据查询条件删除
//        indexWriter.deleteDocuments(NumericRangeQuery.newLongRange("id", Long.parseLong(0+""), Long.parseLong(7+""), true, false));
        //删除所有
        indexWriter.deleteAll();

        indexWriter.commit();
        indexWriter.close();
    }
}
