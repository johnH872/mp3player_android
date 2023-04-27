package hcmute.edu.project_Mp3Player_Nhom06;

import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_CLEAR;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_NEXT;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_PREVIOUS;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_RESUME;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_START;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_PAUSE;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ActionOnMusic;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.IndexSong;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ListSongs;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.MODE_LOOPING_ALBUM;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.MODE_LOOPING_SONG;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.MODE_SHUFFLE;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.PlayStyle;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.PlayerStatus;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.SeekBarData;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.intentName_SeekBarData;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.intentName_ServiceToActivity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;
import hcmute.edu.project_Mp3Player_Nhom06.service.PlayMusicService;

public class PlayMusicActivity extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button btnPlaySong, btnNextSong, btnPreviousSong, btn_playShuffleMode, btn_playLoopOrSingleMode;
    private SeekBar song_slider;
    private TextView time_in_play_music, time_left_play_music;

    private ImageView img_song;
    private TextView song_name, song_singer;

    private Song thisSong;
    private MediaPlayer mMediaPlayer = new MediaPlayer();
    private boolean isPlaying = false;
    //TEST
    private ArrayList<Song> mListSong;
    private int indexOfSong = -1;

    public int playStyle = MODE_LOOPING_ALBUM;
    //
    private boolean checkSeekBarChange = true;
    private BroadcastReceiver seekBarReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                int data = intent.getIntExtra(SeekBarData, -1);
                if(data >= 0){
                    song_slider.setProgress(data);
                    time_in_play_music.setText(convertMillisecondsToMinutesSeconds(data));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Get data form service and send to activity
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                mListSong = (ArrayList<Song>) intent.getSerializableExtra("mListSong");
                indexOfSong = intent.getIntExtra(IndexSong, 0);
                thisSong = mListSong.get(indexOfSong);
                isPlaying = intent.getBooleanExtra(PlayerStatus, false);
                int actionMusic = intent.getIntExtra(ActionOnMusic,0);

                handleLayoutMusic(actionMusic);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_music);
        ///Regis broadcast
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(broadcastReceiver, new IntentFilter(intentName_ServiceToActivity));
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(seekBarReceiver, new IntentFilter(intentName_SeekBarData));
        ///////////////////////////////////////////////////////////////////////////////////////////
        Intent intent = getIntent();
        mListSong = (ArrayList<Song>) intent.getSerializableExtra("mListSong");
        if(mListSong.size() == 0){
            onDestroy();
        }
        //test
        for (Song temp: mListSong) {
            Log.d("Name", temp.getName());
            Log.d("Uri", temp.getMusicResource().toString());
        }
        //
        indexOfSong = intent.getIntExtra("indexSong", -1);
        if(indexOfSong == -1){
            onDestroy();
        }
        ///////////////////////////////////////////////////////////////////////////

        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //clickStopSong();
                onBackPressed();
            }
        });


        btnPlaySong = findViewById(R.id.btn_play);
        btnNextSong = findViewById(R.id.btn_previousSong);
        btnPreviousSong = findViewById(R.id.btn_nextSong);
        btn_playShuffleMode = findViewById(R.id.btn_playShuffleMode);
        btn_playLoopOrSingleMode = findViewById(R.id.btn_playLoopOrSingleMode);
        time_in_play_music = findViewById(R.id.time_in_play_music);
        time_left_play_music = findViewById(R.id.time_left_play_music);

        song_slider = findViewById(R.id.song_slider);

        img_song = findViewById(R.id.song_img);
        song_name = findViewById(R.id.song_name);
        song_singer = findViewById(R.id.song_singer);

        //Event button
        btnPlaySong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isPlaying){ //Pause
                    isPlaying = false;
                    btnPlaySong.setBackgroundResource(R.drawable.ic_play);
                    sendActionToService(ACTION_PAUSE, -1);//-1 equal not change song
                }else{ // Resume
                    isPlaying = true;
                    btnPlaySong.setBackgroundResource(R.drawable.ic_pause_24);
                    sendActionToService(ACTION_RESUME, -1);//-1 equal not change song
                }
            }
        });

        song_slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                time_in_play_music.setText(convertMillisecondsToMinutesSeconds(seekBar.getProgress()));

                Intent intent = new Intent(getApplicationContext(), PlayMusicService.class);
                intent.putExtra(ListSongs, mListSong);
                intent.putExtra(PlayStyle, playStyle);
                intent.putExtra(SeekBarData, seekBar.getProgress());
                intent.putExtra(IndexSong, -1);
                startService(intent);
            }
        });
        //Shuffle mode
        btn_playShuffleMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playStyle = MODE_SHUFFLE;
                sendPlayStyleToService();
                Toast.makeText(getApplicationContext(), "Shuffle Mode", Toast.LENGTH_SHORT).show();
            }
        });
        //Looping mode
        btn_playLoopOrSingleMode.setOnClickListener(view -> {
            switch (playStyle){
                case MODE_LOOPING_SONG:
                    playStyle = MODE_LOOPING_ALBUM;
                    Toast.makeText(getApplicationContext(), "Album looping Mode", Toast.LENGTH_SHORT).show();
                    break;
                case MODE_LOOPING_ALBUM:
                    playStyle = MODE_LOOPING_SONG;
                    Toast.makeText(getApplicationContext(), "Single looping Mode", Toast.LENGTH_SHORT).show();
                    break;
            }
            sendPlayStyleToService();
        });

        btnNextSong.setOnClickListener(view -> {
            isPlaying = true;
            btnPlaySong.setBackgroundResource(R.drawable.ic_pause_24);
            if(indexOfSong + 1 < mListSong.size()){
                indexOfSong ++;
            }else{
                indexOfSong = 0;
            }
            preSetupUI(indexOfSong);
            sendActionToService(ACTION_NEXT, indexOfSong);
        });
        btnPreviousSong.setOnClickListener(view -> {
            isPlaying = true;
            btnPlaySong.setBackgroundResource(R.drawable.ic_pause_24);
            if(indexOfSong - 1 >= 0){
                indexOfSong --;
            }else{
                indexOfSong = mListSong.size() - 1;
            }
            preSetupUI(indexOfSong);
            sendActionToService(ACTION_PREVIOUS, indexOfSong);
        });
        //
        ///Setup UI
        thisSong = mListSong.get(indexOfSong);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), thisSong.getMusicResource());
            mMediaPlayer.prepare();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if(mMediaPlayer != null){
            song_slider.setMax(mMediaPlayer.getDuration());
            time_left_play_music.setText(convertMillisecondsToMinutesSeconds(mMediaPlayer.getDuration()));
            //Image
            ImageView imageView = new ImageView(getApplicationContext());
            img_song.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            Picasso.get().load(thisSong.getImage()).into(imageView, new Callback() { //Load image
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    img_song.setImageBitmap(bitmap);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
            //Singers name
            StringBuilder relatedSingers = new StringBuilder();
            relatedSingers.append("");
            final boolean[] firstSinger = {true};

            List<String> getListSingerRelate = thisSong.getRelatedSingers();
            for (String singerID : getListSingerRelate) {
                readData(new PlayMusicActivity.FireStoreCallback() {
                    @Override
                    public void onCallback(Singer singer) {
                        if(firstSinger[0]) {
                            relatedSingers.append(singer.getName());
                            firstSinger[0] = false;
                        } else {
                            relatedSingers.append(", "+singer.getName());
                        }
                        song_singer.setText(relatedSingers);
                    }
                }, singerID);
            }
            //Song name
            song_name.setText(thisSong.getName());
            handleLayoutMusic(ACTION_START);
        }


        ///////// Chạy nhạc ngay khi vừa mở PlayMusicActivity
        clickStartSong();
        isPlaying = true;
        btnPlaySong.setBackgroundResource(R.drawable.ic_pause_24);
        ////////

    }

    private void sendPlayStyleToService() {
        Intent intent = new Intent(getApplicationContext(), PlayMusicService.class);
        intent.putExtra(ListSongs, mListSong);
        intent.putExtra(PlayStyle, playStyle);
        intent.putExtra(IndexSong, -1);
        startService(intent);
    }

    private void preSetupUI(int index) { //Setup UI
        thisSong = mListSong.get(index);
        mMediaPlayer.reset();
        try{
            mMediaPlayer.setDataSource(getApplicationContext(), thisSong.getMusicResource());
            mMediaPlayer.prepare();
        }catch (Exception e){
            Log.e("MEDIA ERROR", "IO Exception");
        }
        if(mMediaPlayer != null){
            song_slider.setMax(mMediaPlayer.getDuration());
            time_left_play_music.setText(convertMillisecondsToMinutesSeconds(mMediaPlayer.getDuration()));
            //Image
            ImageView imageView = new ImageView(getApplicationContext());
            img_song.setImageBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
            Picasso.get().load(thisSong.getImage()).into(imageView, new Callback() { //Load image
                @Override
                public void onSuccess() {
                    Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                    img_song.setImageBitmap(bitmap);
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
            //Singers name
            StringBuilder relatedSingers = new StringBuilder();
            relatedSingers.append("");
            final boolean[] firstSinger = {true};

            List<String> getListSingerRelate = thisSong.getRelatedSingers();
            for (String singerID : getListSingerRelate) {
                readData(new PlayMusicActivity.FireStoreCallback() {
                    @Override
                    public void onCallback(Singer singer) {
                        if(firstSinger[0]) {
                            relatedSingers.append(singer.getName());
                            firstSinger[0] = false;
                        } else {
                            relatedSingers.append(", "+singer.getName());
                        }
                        song_singer.setText(relatedSingers);
                    }
                }, singerID);
            }
            song_name.setText(thisSong.getName());
            handleLayoutMusic(ACTION_START);
        }
    }

    private void sendActionToService(int action, int indexOfSong) {
        Intent intent = new Intent(this, PlayMusicService.class);

        intent.putExtra(ListSongs, mListSong);
        intent.putExtra(PlayStyle, playStyle);
        intent.putExtra(ActionOnMusic, action);
        intent.putExtra(IndexSong, indexOfSong);

        startService(intent);
    }

    private void clickStartSong(){
        Intent intent = new Intent(this, PlayMusicService.class);

        intent.putExtra(ListSongs, mListSong);
        intent.putExtra(PlayStyle, playStyle);
        intent.putExtra(IndexSong, indexOfSong);

        startService(intent);
    }



    private void handleLayoutMusic(int actionMusic) {
        switch (actionMusic){
            case ACTION_START:
            case ACTION_RESUME:
                isPlaying = true;
                btnPlaySong.setBackgroundResource(R.drawable.ic_pause_24);
                break;
            case ACTION_PAUSE:
            case ACTION_CLEAR:
                isPlaying = false;
                btnPlaySong.setBackgroundResource(R.drawable.ic_play);
                break;
            case ACTION_NEXT:
            case ACTION_PREVIOUS:
                isPlaying = true;
                preSetupUI(indexOfSong);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(seekBarReceiver);
    }

    private String convertMillisecondsToMinutesSeconds(int milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        return String.format("%02d:%02d", minutes, seconds);
    }

    private interface FireStoreCallback {
        void onCallback(Singer singer);
    }
    private void readData(PlayMusicActivity.FireStoreCallback firestoreCallback, String singerID) {
        db.collection("singers").document(singerID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("FireStore Error", "Listen failed.", error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            firestoreCallback.onCallback(value.toObject(Singer.class));
                        } else {
                            Log.d("Null document: ", "Current data: null");
                        }
                    }
                });
    }

}