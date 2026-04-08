package com.example.movieticket84.models;

public class Movie {
    private String id;
    private String title;
    private String posterUrl;
    private String description;
    private String rating; // Score e.g. 8.8
    private String genre;
    private String releaseDate;
    private String duration;
    private String ageRating; // e.g. T16, T13, P

    public Movie() {}

    public Movie(String id, String title, String posterUrl, String description, String rating, String genre, String releaseDate, String duration, String ageRating) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.description = description;
        this.rating = rating;
        this.genre = genre;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.ageRating = ageRating;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getPosterUrl() { return posterUrl; }
    public String getDescription() { return description; }
    public String getRating() { return rating; }
    public String getGenre() { return genre; }
    public String getReleaseDate() { return releaseDate; }
    public String getDuration() { return duration; }
    public String getAgeRating() { return ageRating; }
}