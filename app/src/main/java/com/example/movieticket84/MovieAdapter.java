package com.example.movieticket84;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.movieticket84.models.Movie;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> movieList;
    private OnMovieClickListener listener;

    public interface OnMovieClickListener {
        void onBookClick(Movie movie);
    }

    public MovieAdapter(List<Movie> movieList, OnMovieClickListener listener) {
        this.movieList = movieList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_movie, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movieList.get(position);
        Context context = holder.itemView.getContext();

        holder.tvTitle.setText(movie.getTitle() != null ? movie.getTitle().toUpperCase() : "");
        holder.tvGenre.setText(movie.getGenre());
        holder.tvReleaseDate.setText("Công chiếu: " + movie.getReleaseDate());
        holder.tvDuration.setText(movie.getDuration());
        holder.tvAgeRating.setText(movie.getAgeRating());

        // Xử lý Age Rating color
        String age = movie.getAgeRating() != null ? movie.getAgeRating() : "P";
        if (age.equals("P")) {
            holder.tvAgeRating.setBackgroundResource(R.drawable.bg_tag_green);
        } else if (age.startsWith("T13")) {
            holder.tvAgeRating.setBackgroundResource(R.drawable.bg_tag_yellow);
        } else {
            holder.tvAgeRating.setBackgroundResource(R.drawable.bg_tag_red);
        }

        // Xử lý load ảnh từ Drawable hoặc URL
        String posterUrl = movie.getPosterUrl();
        if (posterUrl != null && posterUrl.startsWith("drawable://")) {
            String resName = posterUrl.replace("drawable://", "");
            int resId = context.getResources().getIdentifier(resName, "drawable", context.getPackageName());
            if (resId != 0) {
                Glide.with(context).load(resId).into(holder.ivPoster);
            } else {
                holder.ivPoster.setImageResource(android.R.drawable.ic_menu_gallery);
            }
        } else {
            Glide.with(context)
                    .load(posterUrl)
                    .placeholder(android.R.drawable.ic_menu_gallery)
                    .into(holder.ivPoster);
        }

        holder.btnBook.setOnClickListener(v -> listener.onBookClick(movie));
    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvTitle, tvGenre, tvReleaseDate, tvDuration, tvAgeRating;
        MaterialButton btnBook;

        public MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            tvReleaseDate = itemView.findViewById(R.id.tvReleaseDate);
            tvDuration = itemView.findViewById(R.id.tvDuration);
            tvAgeRating = itemView.findViewById(R.id.tvAgeRating);
            btnBook = itemView.findViewById(R.id.btnBook);
        }
    }
}