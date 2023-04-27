package hcmute.edu.project_Mp3Player_Nhom06.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

import hcmute.edu.project_Mp3Player_Nhom06.DownloadedSongsActivity;
import hcmute.edu.project_Mp3Player_Nhom06.FavoriteSongsActivity;
import hcmute.edu.project_Mp3Player_Nhom06.LoginActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;


public class PersonalpageFragment extends Fragment implements View.OnClickListener, Toolbar.OnMenuItemClickListener {

    Menu menu;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_personalpage, container, false);
    }

    private Button btn_download, btn_artist, btn_followed, btn_favorite;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Toolbar toolbar = getView().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(this);
        btn_download = view.findViewById(R.id.btn_download);
//        btn_artist = view.findViewById(R.id.btn_artists);
//        btn_followed = view.findViewById(R.id.btn_followed);
        btn_favorite = view.findViewById(R.id.btn_favorite);

        btn_download.setOnClickListener(this);
//        btn_artist.setOnClickListener(this);
//        btn_followed.setOnClickListener(this);
        btn_favorite.setOnClickListener(this);

    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getActivity().getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        Button btn = (Button) view;
        Intent intent;
        if(btn.equals(btn_download)){
            intent = new Intent(this.getContext(), DownloadedSongsActivity.class);
            startActivity(intent);
        }else if(btn.equals(btn_artist)){
            /////
        }else if(btn.equals(btn_favorite)){
            intent = new Intent(this.getContext(), FavoriteSongsActivity.class);
            startActivity(intent);
        }else if(btn.equals(btn_followed)){
            ///
        }else{

        }
    }
}