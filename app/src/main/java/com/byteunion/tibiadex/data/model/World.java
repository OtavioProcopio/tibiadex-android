package com.byteunion.tibiadex.data.model;
public class World {
    public String name;
    public String status;
    public int playersOnline;
    public String location;
    public String pvpType;

    public World(String name, String status, int playersOnline,
                 String location, String pvpType) {
        this.name = name;
        this.status = status;
        this.playersOnline = playersOnline;
        this.location = location;
        this.pvpType = pvpType;
    }
}
