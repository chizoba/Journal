package com.chizoba.journal.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "note")
public class NoteEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String title;
    private String body;
    private Date updatedAt;

    public NoteEntry(int id, String title, String body, Date updatedAt) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.updatedAt = updatedAt;
    }

    @Ignore
    public NoteEntry(String title, String body, Date updatedAt) {
        this.title = title;
        this.body = body;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
