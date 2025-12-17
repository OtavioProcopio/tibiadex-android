package com.byteunion.tibiadex.data.model;

public class House {
    public String name;
    public int houseId;
    public int size;
    public int rent;
    public boolean rented;
    public boolean auctioned;
    public int currentBid;
    public String timeLeft;
    
    // Dados detalhados (preenchidos depois)
    public String world;
    public String town;
    public String type;
    public int beds;
    public String imgUrl;
    public boolean isRented;
    public boolean isAuctioned;
    public String owner;
    public String ownerSex;
    public String paidUntil;
    public String currentBidder;
    public boolean auctionOngoing;
    public String auctionEnd;
    public boolean hasDetailedInfo;
    
    // Construtor para lista básica
    public House(String name, int houseId, int size, int rent, boolean rented, boolean auctioned, int currentBid, String timeLeft) {
        this.name = name;
        this.houseId = houseId;
        this.size = size;
        this.rent = rent;
        this.rented = rented;
        this.auctioned = auctioned;
        this.currentBid = currentBid;
        this.timeLeft = timeLeft;
        this.hasDetailedInfo = false;
    }
    
    // Enriquecer com dados detalhados
    public void enrichWithDetails(String world, String town, String type, int beds, String imgUrl,
                                  boolean isRented, boolean isAuctioned, String owner, String ownerSex,
                                  String paidUntil, String currentBidder, boolean auctionOngoing, String auctionEnd) {
        this.world = world;
        this.town = town;
        this.type = type;
        this.beds = beds;
        this.imgUrl = imgUrl;
        this.isRented = isRented;
        this.isAuctioned = isAuctioned;
        this.owner = owner;
        this.ownerSex = ownerSex;
        this.paidUntil = paidUntil;
        this.currentBidder = currentBidder;
        this.auctionOngoing = auctionOngoing;
        this.auctionEnd = auctionEnd;
        this.hasDetailedInfo = true;
    }
}
