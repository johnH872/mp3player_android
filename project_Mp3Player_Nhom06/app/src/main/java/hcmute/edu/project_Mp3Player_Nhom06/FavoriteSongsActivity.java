package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.SongAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.User;

public class FavoriteSongsActivity extends AppCompatActivity {
    private RecyclerView rclSong;
    private ArrayList<Song> mListSong;
    private ArrayList<String> mListSongId;
    private SongAdapter songAdapter;
    private FirebaseAuth auth;
    private FirebaseFirestore db;
    ProgressBar progressBar;
    Activity contexA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_songs);
        contexA = getParent();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        rclSong = findViewById(R.id.rcl_songs);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rclSong.setLayoutManager(gridLayoutManager);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        mListSong = new ArrayList<>();
        mListSongId = new ArrayList<>();
        songAdapter = new SongAdapter(FavoriteSongsActivity.this, mListSong, mListSongId, contexA);

        rclSong.setAdapter(songAdapter);
        GetFavoriteSongsListener();

        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
    }

    private void GetFavoriteSongsListener() {
        db.collection("users").document(auth.getUid())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                            Log.e("FireStore error", error.getMessage());
                            return;
                        }
                        if (value != null && value.exists()) {
                            User user = value.toObject(User.class);
                            GetSongsListener(user);
                        }
                        songAdapter.notifyDataSetChanged();
                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void GetSongsListener(User user) {
        for (String songId: user.getFavoriteSongs()) {
            db.collection("songs").document(songId)
                    .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                if (progressBar.getVisibility() == View.VISIBLE)
                                    progressBar.setVisibility(View.GONE);
                                Log.e("FireStore error", error.getMessage());
                                return;
                            }
                            if (value != null && value.exists()) {
                                Song song = value.toObject(Song.class);
                                mListSong.add(song);
                                mListSongId.add(value.getId());
                            }
                            songAdapter.notifyDataSetChanged();
                            if (progressBar.getVisibility() == View.VISIBLE)
                                progressBar.setVisibility(View.GONE);
                        }
                    });
        }

    }
}