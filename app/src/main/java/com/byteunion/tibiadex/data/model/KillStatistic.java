package com.byteunion.tibiadex.data.model;

public class KillStatistic {
    public String race;
    public int dayKilled;
    public int dayPlayersKilled;
    public int weekKilled;
    public int weekPlayersKilled;

    public KillStatistic(String race, int dayKilled, int dayPlayersKilled,
                         int weekKilled, int weekPlayersKilled) {
        this.race = race;
        this.dayKilled = dayKilled;
        this.dayPlayersKilled = dayPlayersKilled;
        this.weekKilled = weekKilled;
        this.weekPlayersKilled = weekPlayersKilled;
    }
}

