package cn.itcast.demo;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.io.IOException;

//Lucene的基本入门案例
public class LuceneIndex {
    public static void main(String[] args) throws IOException {
        //1.需要创建indexwriter对象
        //1.1创建 索引库
        FSDirectory directory = FSDirectory.open(new File("/Users/yangxin/test/lucene"));
        //1.2创建 写入器配置对象：参数1 版本号，参数2 分词器
        IndexWriterConfig config = new IndexWriterConfig(Version.LATEST, new IKAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter indexWriter = new IndexWriter(directory, config);

        //2.写入文档
        //2.1创建文档对象
        Document doc = new Document();
        //添加文档属性
        doc.add(new TextField("title", "谷歌地图之父跳槽FaceBook", Field.Store.YES));
        doc.add(new StringField("id", "10", Field.Store.YES));
        doc.add(new TextField("content", "学习", Field.Store.YES));
//        TextField textField = new TextField("content", "学习lucene需要掌握搜索引擎的基本原理和lucene创建索引和查询索引,boots", Field.Store.YES);
//        textField.setBoost(10);
//        doc.add(textField);
        indexWriter.addDocument(doc);

        //3.提交数据
        indexWriter.commit();

        //4.释放资源
        indexWriter.close();
        directory.close();
    }
}
