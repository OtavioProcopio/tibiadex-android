package com.byteunion.tibiadex.data.model;

public class News {
    public int id;
    public String date;
    public String news; // preview text
    public String category;
    public String type;
    public String url;
    
    public News(int id, String date, String news, String category, String type, String url) {
        this.id = id;
        this.date = date;
        this.news = news;
        this.category = category;
        this.type = type;
        this.url = url;
    }
}
