package com.example.movieticket84.models;

public class Ticket {
    private String id;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String showtime;
    private String theaterName;
    private int quantity;
    private double totalPrice;

    public Ticket() {}

    public Ticket(String id, String userId, String movieId, String movieTitle, String showtime, String theaterName, int quantity, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.showtime = showtime;
        this.theaterName = theaterName;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getMovieId() { return movieId; }
    public String getMovieTitle() { return movieTitle; }
    public String getShowtime() { return showtime; }
    public String getTheaterName() { return theaterName; }
    public int getQuantity() { return quantity; }
    public double getTotalPrice() { return totalPrice; }
}