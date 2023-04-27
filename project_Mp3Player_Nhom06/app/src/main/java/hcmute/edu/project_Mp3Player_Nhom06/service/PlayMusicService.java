package hcmute.edu.project_Mp3Player_Nhom06.service;

import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_CLEAR;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_NEXT;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_PAUSE;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_PREVIOUS;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_RESUME;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ACTION_START;
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
import static hcmute.edu.project_Mp3Player_Nhom06.service.MyApplication.CHANNEL_ID;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Random;

import hcmute.edu.project_Mp3Player_Nhom06.PlayMusicActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class PlayMusicService extends Service{


    private MediaPlayer mediaPlayer = new MediaPlayer();
    private boolean isPlaying = true;
    private Song mSong;
    private ArrayList<Song> mListSong;
    private int indexOfSong = -1;

    private int playStyle = 2;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mListSong = (ArrayList<Song>) intent.getSerializableExtra(ListSongs);
        if(mListSong.size() == 0){
            Log.e("Transmit error ","Transmit List Song from activity to service fail");
        }
        playStyle = intent.getIntExtra(PlayStyle, -1);
        if(playStyle == -1){
            Log.e("Transmit error ","Transmit play style from activity to service fail");
        }else{
            Log.d("Transmit play style ",String.valueOf(playStyle));
        }
        indexOfSong = intent.getIntExtra(IndexSong, -1);
        if(indexOfSong == -1){
            Log.e("Transmit index data ",String.valueOf(indexOfSong));
        }else{
            mSong = mListSong.get(indexOfSong);
            isPlaying = true;
            startMusic(mSong);
        }
        sendNotification(mSong);


        int actionMusic = intent.getIntExtra(ActionOnMusic, 0);
        if(actionMusic != 0){
            handleActionMusic(actionMusic);
        }
        int seekBarData = intent.getIntExtra(SeekBarData, -1);
        if(seekBarData != -1){
            onSeekBarChange(seekBarData);
        }
        return START_NOT_STICKY;
    }

    private void onSeekBarChange(int seekBarData) { // Music rewind
        mediaPlayer.seekTo(seekBarData);
    }

    private void handleActionMusic(int action){
        switch (action){
            case ACTION_PAUSE:
                pauseMusic();
                break;
            case ACTION_RESUME:
                resumeMusic();
                break;
            case  ACTION_CLEAR:
                stopSelf();
                sendActionToActivity(ACTION_CLEAR);
                break;
            case ACTION_NEXT:
                changeMusic(mSong, ACTION_NEXT);
                break;
            case ACTION_PREVIOUS:
                changeMusic(mSong, ACTION_PREVIOUS);
                break;
        }
    }

    private void changeMusic(Song song, int action) {
        if(mediaPlayer != null){
            try{
                mediaPlayer.reset();
                mediaPlayer.setDataSource(getApplicationContext(), song.getMusicResource());
                mediaPlayer.prepare();
                UpdateSeekBar();
                mediaPlayer.start();
                isPlaying = true;
                sendNotification(mSong);
                sendActionToActivity(action);
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "JavaIO Exception", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void startMusic(Song song) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), song.getMusicResource());
            mediaPlayer.prepare();
            UpdateSeekBar();
        }catch (Exception e){
            Toast.makeText(getApplicationContext(), "JavaIO Exception", Toast.LENGTH_LONG).show();
        }
        mediaPlayer.start();
        isPlaying = true;
        sendActionToActivity(ACTION_START);
    }
    private void pauseMusic(){
        if(mediaPlayer != null && isPlaying){
            mediaPlayer.pause();
            isPlaying = false;
            sendNotification(mSong);
            sendActionToActivity(ACTION_PAUSE);
        }
    }
    private void resumeMusic() {
        if (mediaPlayer != null && !isPlaying) {
            mediaPlayer.start();
            UpdateSeekBar();
            isPlaying = true;
            sendNotification(mSong);
            sendActionToActivity(ACTION_RESUME);
        }
    }

    private void sendNotification(Song song) {
        Intent intent = new Intent(this, PlayMusicService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_custom_notification);

        // Get image from uri
        ImageView imageView = new ImageView(getApplicationContext());
        remoteViews.setImageViewBitmap(R.id.img_song, Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888));
        Picasso.get().load(song.getImage()).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                remoteViews.setImageViewBitmap(R.id.img_song, bitmap);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
        //

        remoteViews.setTextViewText(R.id.tv_title_song, song.getName());

        remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause_24);
        remoteViews.setImageViewResource(R.id.btn_nextSong, R.drawable.baseline_skip_next_24_black);
        remoteViews.setImageViewResource(R.id.btn_previousSong, R.drawable.baseline_skip_previous_24_black);

        //Click event
        if(isPlaying){
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this,ACTION_PAUSE));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_pause_24);
        } else{
            remoteViews.setOnClickPendingIntent(R.id.img_play_or_pause, getPendingIntent(this,ACTION_RESUME));
            remoteViews.setImageViewResource(R.id.img_play_or_pause, R.drawable.ic_play_notification);
        }
        remoteViews.setOnClickPendingIntent(R.id.btn_nextSong, getPendingIntent(this, ACTION_NEXT));
        remoteViews.setOnClickPendingIntent(R.id.btn_previousSong, getPendingIntent(this, ACTION_PREVIOUS));

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_music_note_24)
                .setContentIntent(pendingIntent) //Where to go when click on notification
                .setCustomContentView(remoteViews)
                .setSound(null)
                .build();

        startForeground(1, notification );
    }
    private PendingIntent getPendingIntent(Context context, int action){ //Send data from notification to service
        Intent intent = new Intent(this, myReceiver.class);
        switch (action){
            case ACTION_NEXT:
                if(indexOfSong + 1 < mListSong.size()){
                    indexOfSong ++;
                }else{
                    indexOfSong = 0;
                }
                break;
            case ACTION_PREVIOUS:
                if(indexOfSong - 1 >= 0){
                    indexOfSong --;
                }else{
                    indexOfSong = mListSong.size() - 1;
                }
                break;
        }
        intent.putExtra(ListSongs, mListSong);
        intent.putExtra(ActionOnMusic, action);
        intent.putExtra(IndexSong, indexOfSong);
        intent.putExtra(PlayStyle, playStyle);

        return PendingIntent.getBroadcast(context.getApplicationContext(), action, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void sendActionToActivity(int action){
        Intent intent = new Intent(intentName_ServiceToActivity);

        intent.putExtra(ListSongs, mListSong);
        intent.putExtra(IndexSong, indexOfSong);
        intent.putExtra(PlayerStatus, isPlaying);
        intent.putExtra(ActionOnMusic, action);

        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    private void UpdateSeekBar(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    Intent intent = new Intent(intentName_SeekBarData);
                    intent.putExtra(SeekBarData, mediaPlayer.getCurrentPosition());
                    LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
                    handler.postDelayed(this, 300);
                    mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            switch (playStyle){
                                case MODE_LOOPING_SONG:
                                    mediaPlayer.setLooping(true);
                                    break;
                                case MODE_LOOPING_ALBUM:
                                    mediaPlayer.setLooping(false);
                                    indexOfSong = (indexOfSong + 1 < mListSong.size())? indexOfSong + 1 : 0;
                                    mSong = mListSong.get(indexOfSong);
                                    mediaPlayer.stop();
                                    handleActionMusic(ACTION_NEXT);
                                    break;
                                case MODE_SHUFFLE:
                                    mediaPlayer.setLooping(false);
                                    Random random = new Random();
                                    indexOfSong = random.nextInt(mListSong.size() - 1);
                                    mSong = mListSong.get(MODE_SHUFFLE);
                                    mediaPlayer.stop();
                                    handleActionMusic(ACTION_NEXT);
                                    break;
                            }
                        }
                    });
                }
            }
        }, 300);
    }
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
