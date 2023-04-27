package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentChange;
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

public class AlbumActivity extends AppCompatActivity {

    private RecyclerView rclSong;
    private ArrayList<Song> mListSong;
    private ArrayList<String> mListSongId;
    ProgressBar progressBar;
    SongAdapter songAdapter;
    FirebaseFirestore db;
    TextView tvAlbumName, tvSongQuantity;
    ImageView imageViewAlbumImage;
    Button btn_playAlbum;
    Context context;
    Activity contextA;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);

        context = getApplicationContext();
        contextA = this.getParent();
        mListSong = new ArrayList<>();
        mListSongId = new ArrayList<>();
        progressBar = findViewById(R.id.progressBar);
        tvAlbumName = findViewById(R.id.album_name);
        tvSongQuantity = findViewById(R.id.album_songQuantity);
        imageViewAlbumImage = findViewById(R.id.album_img);
        btn_playAlbum = findViewById(R.id.btn_playMusic);

        db = FirebaseFirestore.getInstance();

        rclSong = findViewById(R.id.rcl_song);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rclSong.setLayoutManager(gridLayoutManager);

        songAdapter = new SongAdapter(getApplicationContext(),mListSong, mListSongId, contextA);
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
            Album albumItem = (Album) bundle.get("object_album");
            Glide.with(getApplicationContext())
                    .load(albumItem.getImage())
                    .placeholder(R.drawable.alec_album)
                    .error(R.drawable.alec_album)
                    .into(imageViewAlbumImage);

            tvSongQuantity.setText(albumItem.getRelatedSongs().size() + " Songs");
            tvAlbumName.setText(albumItem.getName());
            for (String songId: albumItem.getRelatedSongs()) {
                EventChangeSongListener(songId);
            }
        }else{
            Toast.makeText(this, "NULL", Toast.LENGTH_LONG).show();
        }

        btn_playAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PlayMusicActivity.class);
                intent.putExtra("mListSong", mListSong);
                Bundle bundle = new Bundle();
                bundle.putSerializable("mSong",mListSong.get(0));
                bundle.putInt("indexSong", 0);
                intent.putExtras(bundle);

                context.startActivity(intent);
            }
        });
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