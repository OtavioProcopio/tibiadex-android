package com.byteunion.tibiadex.network;

public class ApiConstants {
    public static final String BASE_URL = "https://api.tibiadata.com/v4";
    
    // Worlds
    public static final String WORLDS = BASE_URL + "/worlds";
    public static String world(String name) {
        return BASE_URL + "/world/" + name;
    }
    
    // Characters
    public static String character(String name) {
        return BASE_URL + "/character/" + java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    // Creatures
    public static final String CREATURES = BASE_URL + "/creatures";
    public static String creature(String race) {
        return BASE_URL + "/creature/" + java.net.URLEncoder.encode(race, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    // Bosses
    public static final String BOOSTABLE_BOSSES = BASE_URL + "/boostablebosses";
    
    // Kill Statistics
    public static String killStatistics(String world) {
        return BASE_URL + "/killstatistics/" + world;
    }
    
    // Spells
    public static final String SPELLS = BASE_URL + "/spells";
    public static String spell(String name) {
        return BASE_URL + "/spell/" + java.net.URLEncoder.encode(name, java.nio.charset.StandardCharsets.UTF_8);
    }
    
    // Highscores
    public static String highscores(String world, String category, String vocation, int page) {
        return BASE_URL + "/highscores/" + world + "/" + category + "/" + vocation + "/" + page;
    }
    
    // Fansites
    public static final String FANSITES = BASE_URL + "/fansites";
    
    // Houses
    public static String houses(String world, String town) {
        return BASE_URL + "/houses/" + world + "/" + town;
    }
    public static String house(String world, int houseId) {
        return BASE_URL + "/house/" + world + "/" + houseId;
    }
    
    // News
    public static String newsArchive(int days) {
        return BASE_URL + "/news/archive/" + days;
    }
    public static String newsById(int id) {
        return BASE_URL + "/news/id/" + id;
    }
}
