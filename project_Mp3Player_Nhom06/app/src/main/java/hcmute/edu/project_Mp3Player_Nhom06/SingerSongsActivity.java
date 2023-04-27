package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.SongAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class SingerSongsActivity extends AppCompatActivity {

    private RecyclerView rclSong;
    FirebaseFirestore db;
    ArrayList<Song> mListSong;
    ArrayList<String> mListSongId;
    ProgressBar progressBar;
    SongAdapter songAdapter;
    TextView tvSingerName, tvSongQuantity;
    ImageFilterView imgFilterViewSingerWallpaper;
    Activity contexA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singer_songs);
        contexA = getParent();
        mListSong = new ArrayList<>();
        mListSongId = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        tvSingerName = findViewById(R.id.singer_Name);
        tvSongQuantity = findViewById(R.id.singer_songQuantity);
        imgFilterViewSingerWallpaper = findViewById(R.id.singerWallpaper);

        rclSong = findViewById(R.id.rcl_songs);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rclSong.setLayoutManager(gridLayoutManager);

        songAdapter = new SongAdapter(getApplicationContext(),mListSong, mListSongId, contexA);
        rclSong.setAdapter(songAdapter);

        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            Singer albumItem = (Singer) bundle.get("object_singer");
            Glide.with(getApplicationContext())
                    .load(albumItem.getWallpaper())
                    .placeholder(R.drawable.alec_album)
                    .error(R.drawable.alec_album)
                    .into(imgFilterViewSingerWallpaper);

            tvSongQuantity.setText(albumItem.getRelatedSongs().size() + " Songs");
            tvSingerName.setText(albumItem.getName());
            for (String songId: albumItem.getRelatedSongs()) {
                EventChangeSongListener(songId);
            }
        }else{
            Toast.makeText(this, "NULL", Toast.LENGTH_LONG).show();
        }
    }

    private void EventChangeSongListener(String songId) {
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
                        } else {
                            Log.d("Null document: ", "Current data: null");
                        }
                        songAdapter.notifyDataSetChanged();
                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }
}