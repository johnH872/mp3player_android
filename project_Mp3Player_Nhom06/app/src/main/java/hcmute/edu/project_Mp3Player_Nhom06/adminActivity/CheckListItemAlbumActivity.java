package hcmute.edu.project_Mp3Player_Nhom06.adminActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter.ListCheckboxAlbumAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;

public class CheckListItemAlbumActivity extends AppCompatActivity {
    ArrayList<Album> mListAlbum;
    ArrayList<String> mListAlbumId;
    HashMap<String, Album> mListAlbumAdd;
    RecyclerView recyclerView;
    ListCheckboxAlbumAdapter listCheckboxAlbumAdapter;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    ListCheckboxAlbumAdapter.OnItemCheckListener onItemCheckListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_item);
        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
        fStore = FirebaseFirestore.getInstance();
        mListAlbum = new ArrayList<>();
        mListAlbumId = new ArrayList<>();
        mListAlbumAdd = new HashMap<>();

        Intent intentFromAddNewSongActivity = getIntent();
        mListAlbumAdd = (HashMap<String, Album>) intentFromAddNewSongActivity.getSerializableExtra("allAddAlbum");

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rcl_listAdd);

        onItemCheckListener = new ListCheckboxAlbumAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Album Album, String id) {
                mListAlbumAdd.put(id, Album);
            }

            @Override
            public void onItemUnCheck(Album Album, String id) {
                mListAlbumAdd.remove(id);
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        listCheckboxAlbumAdapter = new ListCheckboxAlbumAdapter(mListAlbum, mListAlbumId, mListAlbumAdd, this, onItemCheckListener);

        recyclerView.setAdapter(listCheckboxAlbumAdapter);
        EventChangeAlbumListener();

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        Intent intent = new Intent();
                        intent.putExtra("allAddAlbum", mListAlbumAdd);
                        setResult(RESULT_OK, intent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void EventChangeAlbumListener() {
        fStore.collection("albums").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        Album Album = dc.getDocument().toObject(Album.class);
                        mListAlbum.add(Album);
                        mListAlbumId.add(dc.getDocument().getId());
                    }
                }
                listCheckboxAlbumAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}