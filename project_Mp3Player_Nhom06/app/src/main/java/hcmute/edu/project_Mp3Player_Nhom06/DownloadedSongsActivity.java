package hcmute.edu.project_Mp3Player_Nhom06;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.SongAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class DownloadedSongsActivity extends AppCompatActivity {
    ArrayList<Song> arrayListSong = new ArrayList<>();
    private RecyclerView rclSong;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloaded_songs);
        arrayListSong = getListSong();
        rclSong = findViewById(R.id.rcl_songs);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        rclSong.setLayoutManager(gridLayoutManager);

        SongAdapter songAdapter = new SongAdapter(this , arrayListSong, getListSongId(),this );
        rclSong.setAdapter(songAdapter);

        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private ArrayList<String> getListSongId() {
        int i = arrayListSong.size();
        ArrayList<String> tempList = new ArrayList<>();;
        for(int t = 0; t< i; t++){
            tempList.add(String.valueOf(i));
        }
        return tempList;
    }

    private ArrayList<Song> getListSong() {
        final ArrayList<Song> tempAudioList = new ArrayList<>();
        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor c = getApplicationContext().getContentResolver().query(allSongsUri, null, selection, null, null);
        if (c != null){
            while (c.moveToNext()) {
                Song audioModel = new Song();
                @SuppressLint("Range") String path = c.getString(c.getColumnIndex(MediaStore.Audio.Media.DATA));
                String name = "ARTIST";
                String album = "";
                String artist = "";

                audioModel.setMusicResource(path);
                audioModel.setName(name);
                audioModel.setImage("");


                Log.e("Name :" + name, " Album :" + album);
                Log.e("Path :" + path, " Artist :" + artist);

                tempAudioList.add(audioModel);

            }
        }
        c.close();
        Log.d("Size:",String.valueOf(tempAudioList.size()));
        Toast.makeText(this,String.valueOf(tempAudioList.size()), Toast.LENGTH_LONG).show();
        return tempAudioList;
    }
}