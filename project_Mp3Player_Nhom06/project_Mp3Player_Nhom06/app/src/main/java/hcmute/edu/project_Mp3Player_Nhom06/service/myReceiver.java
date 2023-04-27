package hcmute.edu.project_Mp3Player_Nhom06.service;

import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ActionOnMusic;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.IndexSong;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.ListSongs;
import static hcmute.edu.project_Mp3Player_Nhom06.Constants.Constants.PlayStyle;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class myReceiver extends BroadcastReceiver { //Get data from notification and send to service
    @Override
    public void onReceive(Context context, Intent intent) {
        ArrayList<Song> mListSong = (ArrayList<Song>) intent.getSerializableExtra("mListSong");
        if(mListSong.size() == 0){
            Log.e("Transmit error", "Transmit to myReceiver error");
        }
        int actionMusic = intent.getIntExtra(ActionOnMusic, 0);
        if(actionMusic == 0){
            Log.e("Transmit error", "Transmit to myReceiver error");
        }
        int indexSong = intent.getIntExtra(IndexSong, -1);
        if(indexSong == -1){
            Log.e("Transmit error", "Transmit to myReceiver error");
        }
        int playStyle = intent.getIntExtra(PlayStyle, 0);
        if(playStyle == 0){
            Log.e("Transmit error", "Transmit to myReceiver error");
        }


        Intent intentService = new Intent(context, PlayMusicService.class);
        intentService.putExtra(ActionOnMusic, actionMusic);
        intentService.putExtra(IndexSong, indexSong);
        intentService.putExtra(ListSongs, mListSong);
        intentService.putExtra(PlayStyle, playStyle);

        context.startService(intentService);
    }
}
