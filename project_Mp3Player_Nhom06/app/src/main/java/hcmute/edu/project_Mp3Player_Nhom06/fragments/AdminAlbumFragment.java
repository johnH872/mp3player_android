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
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.AddNewAlbumActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter.AdminAlbumAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;

public class AdminAlbumFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    SearchView searchView;
    FirebaseFirestore db;
    AdminAlbumAdapter albumAdapter;
    ArrayList<Album> mListAlbum;
    ArrayList<String> mListAlbumId;
    RecyclerView recyclerViewAlbums;
    ProgressBar progressBar;
    ExtendedFloatingActionButton btn_addMore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_album, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mListAlbum = new ArrayList<>();
        mListAlbumId = new ArrayList<>();
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        btn_addMore = view.findViewById(R.id.btn_addMore);
        recyclerViewAlbums = view.findViewById(R.id.rcl_albums);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewAlbums.setLayoutManager(linearLayoutManager);
        albumAdapter = new AdminAlbumAdapter(view.getContext(), mListAlbum, mListAlbumId);
        recyclerViewAlbums.setAdapter(albumAdapter);
        EventChangeSingerListener();

        Toolbar toolbar = getView().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(this);

        btn_addMore.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), AddNewAlbumActivity.class);
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
        ArrayList<Album> filteredListAlbum = new ArrayList<>();
        ArrayList<String> filteredListId = new ArrayList<>();
        for (Album album: mListAlbum) {
            if (album.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredListAlbum.add(album);
                filteredListId.add(mListAlbumId.get(mListAlbum.indexOf(album)));
            }
            if (!filteredListAlbum.isEmpty()) albumAdapter.setFilteredListAlbum(filteredListAlbum, filteredListId);
        }
    }

    private void EventChangeSingerListener() {
        db.collection("albums").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        Album album = dc.getDocument().toObject(Album.class);
                        mListAlbum.add(album);
                        mListAlbumId.add(dc.getDocument().getId());
                    }
                }
                albumAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}