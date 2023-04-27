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
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter.ListCheckboxSingerAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;

public class CheckListItemSingerActivity extends AppCompatActivity {
    ArrayList<Singer> mListSinger;
    ArrayList<String> mListSingerId;
    HashMap<String, Singer> mListSingerAdd;
    RecyclerView recyclerView;
    ListCheckboxSingerAdapter listCheckboxSingerAdapter;
    FirebaseFirestore fStore;
    ProgressBar progressBar;
    ListCheckboxSingerAdapter.OnItemCheckListener onItemCheckListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list_item);
        Toolbar toolBar = findViewById(R.id.topAppBar);
        toolBar.setNavigationOnClickListener(view -> onBackPressed());
        fStore = FirebaseFirestore.getInstance();
        mListSinger = new ArrayList<>();
        mListSingerId = new ArrayList<>();
        mListSingerAdd = new HashMap<>();

        Intent intentFromAddNewSongActivity = getIntent();
        mListSingerAdd = (HashMap<String, Singer>) intentFromAddNewSongActivity.getSerializableExtra("allAddSinger");

        progressBar = findViewById(R.id.progressBar);
        recyclerView = findViewById(R.id.rcl_listAdd);

        onItemCheckListener = new ListCheckboxSingerAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Singer singer, String id) {
                mListSingerAdd.put(id, singer);
                Log.e("Added", mListSingerAdd.toString());
            }

            @Override
            public void onItemUnCheck(Singer singer, String id) {
                mListSingerAdd.remove(id);
                Log.e("Removed", mListSingerAdd.toString());
            }
        };

        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        listCheckboxSingerAdapter = new ListCheckboxSingerAdapter(mListSinger, mListSingerId, mListSingerAdd, this, onItemCheckListener);

        recyclerView.setAdapter(listCheckboxSingerAdapter);
        EventChangeSingerListener();

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.save:
                        Intent intent = new Intent();
                        intent.putExtra("allAddSinger", mListSingerAdd);
                        setResult(RESULT_OK, intent);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    private void EventChangeSingerListener() {
        fStore.collection("singers").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        Singer singer = dc.getDocument().toObject(Singer.class);
                        mListSinger.add(singer);
                        mListSingerId.add(dc.getDocument().getId());
                    }
                }
                listCheckboxSingerAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}