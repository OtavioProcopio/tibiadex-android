package com.byteunion.tibiadex.data.model;

import java.util.List;

public class Spell {
    public String name;
    public String spellId;
    public String formula;
    public int level;
    public int mana;
    public int price;
    public boolean groupAttack;
    public boolean groupHealing;
    public boolean groupSupport;
    public boolean typeInstant;
    public boolean typeRune;
    public boolean premiumOnly;
    
    // Campos detalhados (obtidos via endpoint /spell/{id})
    public String imageUrl;
    public List<String> vocations;
    public List<String> cities;
    public int cooldownAlone;
    public int cooldownGroup;
    public boolean hasDetailedInfo;

    // Construtor com dados básicos da lista
    public Spell(String name, String spellId, String formula, int level, int mana, 
                 int price, boolean groupAttack, boolean groupHealing, boolean groupSupport,
                 boolean typeInstant, boolean typeRune, boolean premiumOnly) {
        this.name = name;
        this.spellId = spellId;
        this.formula = formula;
        this.level = level;
        this.mana = mana;
        this.price = price;
        this.groupAttack = groupAttack;
        this.groupHealing = groupHealing;
        this.groupSupport = groupSupport;
        this.typeInstant = typeInstant;
        this.typeRune = typeRune;
        this.premiumOnly = premiumOnly;
        this.hasDetailedInfo = false;
    }

    // Método para enriquecer com dados detalhados
    public void enrichWithDetails(String imageUrl, List<String> vocations, 
                                   List<String> cities, int cooldownAlone, int cooldownGroup) {
        this.imageUrl = imageUrl;
        this.vocations = vocations;
        this.cities = cities;
        this.cooldownAlone = cooldownAlone;
        this.cooldownGroup = cooldownGroup;
        this.hasDetailedInfo = true;
    }
}
