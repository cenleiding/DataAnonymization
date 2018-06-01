package com.CLD.dataAnonymization.dao.h2.entity;

import javax.persistence.*;

/**
 * @Author CLD
 * @Date 2018/5/31 16:32
 **/
@Entity
@Table(name = "DbInfo")
public class DbInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String url;

    @Column(length = 2500)
    private String state;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
