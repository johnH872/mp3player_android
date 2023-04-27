package hcmute.edu.project_Mp3Player_Nhom06.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import hcmute.edu.project_Mp3Player_Nhom06.LoginActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.AddNewSongActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter.AdminSongAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class AdminSongsFragment extends Fragment implements Toolbar.OnMenuItemClickListener {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_songs, container, false);
    }
    SearchView searchView;
    FirebaseFirestore db;
    AdminSongAdapter songAdapter;
    ArrayList<Song> mListSong;
    ArrayList<String> mListSongId;
    RecyclerView recyclerViewSongs;
    ProgressBar progressBar;
    ExtendedFloatingActionButton btn_addMore;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mListSong = new ArrayList<>();
        mListSongId = new ArrayList<>();
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        btn_addMore = view.findViewById(R.id.btn_addMore);
        recyclerViewSongs = view.findViewById(R.id.rcl_songs);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSongs.setLayoutManager(linearLayoutManager);
        songAdapter = new AdminSongAdapter(view.getContext(), mListSong, mListSongId);
        recyclerViewSongs.setAdapter(songAdapter);
        EventChangeSongListener();

        Toolbar toolbar = getView().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(this);

        btn_addMore.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), AddNewSongActivity.class);
            startActivity(intent);
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
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

    private void filterList(String newText) {
        ArrayList<Song> filteredListSong = new ArrayList<>();
        ArrayList<String> filteredListId = new ArrayList<>();
        for (Song song: mListSong) {
            if (song.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredListSong.add(song);
                filteredListId.add(mListSongId.get(mListSong.indexOf(song)));
            }
            if (!filteredListSong.isEmpty()) songAdapter.setFilteredListSong(filteredListSong, filteredListId);
        }
    }

    private void EventChangeSongListener() {
        db.collection("songs").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    if (progressBar.getVisibility() == View.VISIBLE)
                        progressBar.setVisibility(View.GONE);
                    Log.e("FireStore error", error.getMessage());
                    return;
                }
                for (DocumentChange dc : value.getDocumentChanges()) {
                    if (dc.getType() == DocumentChange.Type.ADDED) {
                        Song song = dc.getDocument().toObject(Song.class);
                        mListSong.add(song);
                        mListSongId.add(dc.getDocument().getId());
                    }
                }
                songAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}

