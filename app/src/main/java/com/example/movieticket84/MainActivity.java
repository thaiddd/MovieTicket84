package com.example.movieticket84;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movieticket84.models.Movie;
import com.example.movieticket84.models.Showtime;
import com.example.movieticket84.models.Theater;
import com.example.movieticket84.models.Ticket;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private List<Movie> movieList = new ArrayList<>();
    private List<Theater> theaterList = new ArrayList<>();
    private List<Showtime> showtimeList = new ArrayList<>();
    
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FloatingActionButton btnLogout;
    private TextView tvSubtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        rvMovies = findViewById(R.id.rvMovies);
        btnLogout = findViewById(R.id.btnLogout);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        
        adapter = new MovieAdapter(movieList, this::showBookingDialog);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));
        rvMovies.setAdapter(adapter);

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        });

        btnLogout.setOnLongClickListener(v -> {
            seedRichDatabase();
            return true;
        });

        loadData();
    }

    private void loadData() {
        db.collection("movies").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                movieList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    movieList.add(document.toObject(Movie.class));
                }
                
                if (movieList.isEmpty() || !movieList.get(0).getPosterUrl().contains("henemngaynhatthuc")) {
                    seedRichDatabase();
                } else {
                    adapter.notifyDataSetChanged();
                    updateSubtitle();
                }
            }
        });

        db.collection("theaters").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                theaterList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    theaterList.add(document.toObject(Theater.class));
                }
                updateSubtitle();
            }
        });

        db.collection("showtimes").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                showtimeList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    showtimeList.add(document.toObject(Showtime.class));
                }
            }
        });
    }

    private void updateSubtitle() {
        if (tvSubtitle != null) {
            tvSubtitle.setText("Danh sách " + movieList.size() + " phim từ " + theaterList.size() + " rạp chiếu");
        }
    }

    private void seedRichDatabase() {
        Toast.makeText(this, "Đang nạp ảnh từ drawable và dữ liệu mới...", Toast.LENGTH_SHORT).show();
        WriteBatch batch = db.batch();

        List<Movie> newMovies = new ArrayList<>();
        // Sử dụng ảnh từ drawable cho 3 phim đầu
        newMovies.add(new Movie("m1", "HẸN EM NGÀY NHẬT THỰC", "drawable://henemngaynhatthuc", "Phim tình cảm đặc sắc.", "8.8", "Tình cảm, Gia đình", "03/04/2026", "118 phút", "T16"));
        newMovies.add(new Movie("m2", "SONG HỶ LÂM NGUY", "drawable://songhylamnguy", "Phim hài hước dí dỏm.", "8.6", "Hài, Tâm lý, Gia đình", "03/04/2026", "113 phút", "T13"));
        newMovies.add(new Movie("m3", "PHIM SUPER MARIO THIÊN HÀ", "drawable://mario", "Hành động hoạt hình cho bé.", "9.0", "Hài, Hành động, Hoạt hình", "01/04/2026", "99 phút", "P"));
        newMovies.add(new Movie("m4", "AVATAR: DÒNG CHẢY NƯỚC", "https://picsum.photos/id/40/300/450", "Siêu phẩm viễn tưởng.", "7.7", "Viễn tưởng, Hành động", "15/12/2025", "190 phút", "T13"));
        newMovies.add(new Movie("m5", "OPPENHEIMER", "https://picsum.photos/id/50/300/450", "Phim lịch sử kịch tính.", "8.4", "Lịch sử, Chính kịch", "21/07/2025", "180 phút", "T16"));

        for (Movie m : newMovies) {
            batch.set(db.collection("movies").document(m.getId()), m);
        }

        List<Theater> newTheaters = new ArrayList<>();
        newTheaters.add(new Theater("t1", "CGV Aeon Hà Đông", "Hà Nội"));
        newTheaters.add(new Theater("t2", "Lotte Cinema Landmark", "Hà Nội"));
        newTheaters.add(new Theater("t3", "BHD Star Phạm Ngọc Thạch", "Hà Nội"));
        newTheaters.add(new Theater("t4", "Beta Cinemas Giải Phóng", "Hà Nội"));

        for (Theater t : newTheaters) {
            batch.set(db.collection("theaters").document(t.getId()), t);
        }

        String[] times = {"17:25", "18:40", "19:50", "20:45", "21:15"};
        int sId = 1;
        for (Movie m : newMovies) {
            for (Theater t : newTheaters) {
                for (String time : times) {
                    String docId = m.getId() + "_" + t.getId() + "_" + time.replace(":", "");
                    Showtime s = new Showtime(docId, m.getId(), t.getId(), time + " - Hôm nay", 90000);
                    batch.set(db.collection("showtimes").document(s.getId()), s);
                    sId++;
                }
            }
        }

        batch.commit().addOnSuccessListener(aVoid -> {
            loadData();
            Toast.makeText(this, "Đã cập nhật ảnh và giờ chiếu thành công!", Toast.LENGTH_LONG).show();
        });
    }

    private void showBookingDialog(Movie movie) {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_booking, null);
        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        AutoCompleteTextView spTheater = view.findViewById(R.id.spinnerTheater);
        AutoCompleteTextView spShowtime = view.findViewById(R.id.spinnerShowtime);

        tvTitle.setText(movie.getTitle());

        ArrayAdapter<Theater> theaterAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, theaterList);
        spTheater.setAdapter(theaterAdapter);

        spTheater.setOnItemClickListener((parent, v, position, id) -> {
            Theater selectedTheater = (Theater) parent.getItemAtPosition(position);
            List<Showtime> filteredShowtimes = new ArrayList<>();
            for (Showtime s : showtimeList) {
                if (s.getMovieId().equals(movie.getId()) && s.getTheaterId().equals(selectedTheater.getId())) {
                    filteredShowtimes.add(s);
                }
            }
            ArrayAdapter<Showtime> showtimeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, filteredShowtimes);
            spShowtime.setAdapter(showtimeAdapter);
            spShowtime.setText("", false);
        });

        new MaterialAlertDialogBuilder(this)
                .setView(view)
                .setPositiveButton("ĐẶT VÉ", (dialog, which) -> {
                    String theaterName = spTheater.getText().toString();
                    String showtimeStr = spShowtime.getText().toString();
                    if (!theaterName.isEmpty() && !showtimeStr.isEmpty()) {
                        performBooking(movie, theaterName, showtimeStr);
                    }
                })
                .setNegativeButton("Đóng", null)
                .show();
    }

    private void performBooking(Movie movie, String theaterName, String showtimeStr) {
        String ticketId = UUID.randomUUID().toString();
        Ticket ticket = new Ticket(ticketId, mAuth.getUid(), movie.getId(), movie.getTitle(), showtimeStr, theaterName, 1, 90000.0);
        
        db.collection("tickets").document(ticketId).set(ticket)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Đặt vé thành công!", Toast.LENGTH_LONG).show();
                    sendLocalNotification(movie.getTitle(), "Suất chiếu: " + showtimeStr + " tại " + theaterName);
                });
    }

    private void sendLocalNotification(String title, String message) {
        String channelId = "movie_notifications";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Movie Reminders", NotificationManager.IMPORTANCE_HIGH);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});
            channel.enableVibration(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}