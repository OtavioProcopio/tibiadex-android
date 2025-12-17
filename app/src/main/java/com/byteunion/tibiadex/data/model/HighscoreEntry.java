package com.byteunion.tibiadex.data.model;

public class HighscoreEntry {
    public int rank;
    public String name;
    public String vocation;
    public String world;
    public int level;
    public int value; // Valor da categoria (exp, fishing skill, etc)

    public HighscoreEntry(int rank, String name, String vocation, 
                          String world, int level, int value) {
        this.rank = rank;
        this.name = name;
        this.vocation = vocation;
        this.world = world;
        this.level = level;
        this.value = value;
    }
}
