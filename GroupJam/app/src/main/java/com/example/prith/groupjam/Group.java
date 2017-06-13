package com.example.prith.groupjam;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by prith on 6/12/2017.
 */

public class Group {
    private String creator;
    private Date createTime;
    private String genre;
    private String name;
    private boolean isPrivate;
    private String accessCode;

    public Group(){
        //empty constructor
    }

    public Group(String creator, String genre, String name, boolean isPrivate, String accessCode, Date time) {
        this.creator = creator;
        this.genre = genre;
        this.name = name;
        this.isPrivate = isPrivate;
        this.accessCode = accessCode;
        createTime = time;
    }

    public String getCreator() {
        return creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getGenre() {
        return genre;
    }

    public String getName() {
        return name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public String getAccessCode() {
        return accessCode;
    }
}
