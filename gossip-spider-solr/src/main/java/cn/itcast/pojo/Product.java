package cn.itcast.pojo;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;

public class Product implements Serializable {
    @Field
    private String id;
    @Field
    private String name;
    @Field
    private String title;
    @Field
    private String content;

    public Product() {
    }

    public Product(String id, String name, String title, String content) {
        this.id = id;
        this.name = name;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContext() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
