package cn.itcast.dao;

import cn.itcast.pojo.News;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.beans.PropertyVetoException;

public class NewsDao extends JdbcTemplate {
    private static ComboPooledDataSource dataSource;
    static {
        dataSource = new ComboPooledDataSource();
        try {
            dataSource.setDriverClass("com.mysql.jdbc.Driver");
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        dataSource.setUser("root");
        dataSource.setPassword("admin");
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/gossip?characterEncoding=utf-8");
    }

    public NewsDao() {
        super.setDataSource(dataSource);
    }
    //添加数据的操作

    public void  addNews(News news){
        String sql = "insert into  news values (?,?,?,?,?,?,?)";
        update(sql,news.getTitle(),news.getDocurl(),news.getTime(),news.getLabel(),news.getSource(),news.getContent(),news.getEditor());
    }

}
