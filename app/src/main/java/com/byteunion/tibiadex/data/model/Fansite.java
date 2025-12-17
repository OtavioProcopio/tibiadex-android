package com.byteunion.tibiadex.data.model;

import java.util.List;

public class Fansite {
    public String name;
    public String logoUrl;
    public String homepage;
    public String contact;
    
    // Content types
    public boolean statistics;
    public boolean texts;
    public boolean tools;
    public boolean wiki;
    
    // Social media
    public boolean discord;
    public boolean facebook;
    public boolean instagram;
    public boolean reddit;
    public boolean twitch;
    public boolean twitter;
    public boolean youtube;
    
    public List<String> languages;
    public List<String> specials;
    public boolean fansiteItem;
    public String fansiteItemUrl;

    public Fansite(String name, String logoUrl, String homepage, String contact) {
        this.name = name;
        this.logoUrl = logoUrl;
        this.homepage = homepage;
        this.contact = contact;
    }
}
