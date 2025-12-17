package com.byteunion.tibiadex.data.model;

import java.util.List;

public class Creature {
    public String name;
    public String race;
    public String imageUrl;
    public boolean featured;
    
    // Campos detalhados (obtidos via /creature/{race})
    public String description;
    public String behaviour;
    public int hitpoints;
    public int experiencePoints;
    public boolean isLootable;
    public List<String> immune;
    public List<String> strong;
    public List<String> weakness;
    public List<String> lootList;
    public boolean beParalysed;
    public boolean beSummoned;
    public boolean beConvinced;
    public boolean seeInvisible;
    public boolean hasDetailedInfo;

    // Construtor com dados básicos da lista
    public Creature(String name, String race, String imageUrl, boolean featured) {
        this.name = name;
        this.race = race;
        this.imageUrl = imageUrl;
        this.featured = featured;
        this.hasDetailedInfo = false;
    }

    // Método para enriquecer com dados detalhados
    public void enrichWithDetails(String description, String behaviour, int hitpoints,
                                   int experiencePoints, boolean isLootable,
                                   List<String> immune, List<String> strong, List<String> weakness,
                                   List<String> lootList, boolean beParalysed, boolean beSummoned,
                                   boolean beConvinced, boolean seeInvisible) {
        this.description = description;
        this.behaviour = behaviour;
        this.hitpoints = hitpoints;
        this.experiencePoints = experiencePoints;
        this.isLootable = isLootable;
        this.immune = immune;
        this.strong = strong;
        this.weakness = weakness;
        this.lootList = lootList;
        this.beParalysed = beParalysed;
        this.beSummoned = beSummoned;
        this.beConvinced = beConvinced;
        this.seeInvisible = seeInvisible;
        this.hasDetailedInfo = true;
    }
}
