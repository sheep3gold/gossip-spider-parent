package com.ittest.redistest;

import java.util.Date;

public class Club {
    private int id;
    private String name;
    private String info;
    private Date createDate;
    private int rank;

    public Club(int id, String name, String info, Date createDate, int rank) {
        this.id = id;
        this.name = name;
        this.info = info;
        this.createDate = createDate;
        this.rank = rank;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }
}
