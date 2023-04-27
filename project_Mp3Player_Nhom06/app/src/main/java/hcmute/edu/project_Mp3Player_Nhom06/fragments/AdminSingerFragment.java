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
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.AddNewSingerActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter.AdminSingerAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;

public class AdminSingerFragment extends Fragment implements Toolbar.OnMenuItemClickListener {
    SearchView searchView;
    FirebaseFirestore db;
    AdminSingerAdapter SingerAdapter;
    ArrayList<Singer> mListSinger;
    ArrayList<String> mListSingerId;
    RecyclerView recyclerViewSingers;
    ProgressBar progressBar;
    ExtendedFloatingActionButton btn_addMore;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_singer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = FirebaseFirestore.getInstance();
        mListSinger = new ArrayList<>();
        mListSingerId = new ArrayList<>();
        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        btn_addMore = view.findViewById(R.id.btn_addMore);
        recyclerViewSingers = view.findViewById(R.id.rcl_singers);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewSingers.setLayoutManager(linearLayoutManager);
        SingerAdapter = new AdminSingerAdapter(view.getContext(), mListSinger, mListSingerId);
        recyclerViewSingers.setAdapter(SingerAdapter);
        EventChangeSingerListener();

        Toolbar toolbar = getView().findViewById(R.id.topAppBar);
        toolbar.setOnMenuItemClickListener(this);

        btn_addMore.setOnClickListener(view1 -> {
            Intent intent = new Intent(view.getContext(), AddNewSingerActivity.class);
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
        ArrayList<Singer> filteredListSinger = new ArrayList<>();
        ArrayList<String> filteredListId = new ArrayList<>();
        for (Singer Singer: mListSinger) {
            if (Singer.getName().toLowerCase().contains(newText.toLowerCase())) {
                filteredListSinger.add(Singer);
                filteredListId.add(mListSingerId.get(mListSinger.indexOf(Singer)));
            }
            if (!filteredListSinger.isEmpty()) SingerAdapter.setFilteredListSinger(filteredListSinger, filteredListId);
        }
    }

    private void EventChangeSingerListener() {
        db.collection("singers").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                        Singer Singer = dc.getDocument().toObject(Singer.class);
                        mListSinger.add(Singer);
                        mListSingerId.add(dc.getDocument().getId());
                    }
                }
                SingerAdapter.notifyDataSetChanged();
                if (progressBar.getVisibility() == View.VISIBLE)
                    progressBar.setVisibility(View.GONE);
            }
        });
    }
}