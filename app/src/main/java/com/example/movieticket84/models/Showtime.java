package com.example.movieticket84.models;

public class Showtime {
    private String id;
    private String movieId;
    private String theaterId;
    private String time; // e.g., "20:00, Oct 25"
    private double price;

    public Showtime() {}

    public Showtime(String id, String movieId, String theaterId, String time, double price) {
        this.id = id;
        this.movieId = movieId;
        this.theaterId = theaterId;
        this.time = time;
        this.price = price;
    }

    public String getId() { return id; }
    public String getMovieId() { return movieId; }
    public String getTheaterId() { return theaterId; }
    public String getTime() { return time; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return time + " - $" + price;
    }
}