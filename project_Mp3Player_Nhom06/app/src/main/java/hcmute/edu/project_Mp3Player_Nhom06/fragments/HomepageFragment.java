package hcmute.edu.project_Mp3Player_Nhom06.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.SongAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.AlbumItemAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.ArtistItemAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.RecommendItem;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.RecommendItemAdapter;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class HomepageFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_homepage, container, false);
    }

    private RecyclerView rcvAlbum, rcvArtists, rcvRecom;
    private EditText editText;

    private List<Album> mListAlbum;
    private List<Singer> mListSinger;
    private List<RecommendItem> mListRecom;

    private AlbumItemAdapter albumItemAdapter;
    private ArtistItemAdapter artistItemAdapter;
    private RecommendItemAdapter recommendItemAdapter;

    private FirebaseFirestore db;
    ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        db = FirebaseFirestore.getInstance();
        mListAlbum = new ArrayList<>();
        mListSinger = new ArrayList<>();
        mListRecom = new ArrayList<>();

        //Album display
        rcvAlbum = view.findViewById(R.id.rcv_albumList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcvAlbum.setLayoutManager(linearLayoutManager);
        albumItemAdapter = new AlbumItemAdapter(this.getContext(), mListAlbum); ///
        rcvAlbum.setAdapter(albumItemAdapter);
        EvenChangeAlbumListener();

        //Artist display
        rcvArtists = view.findViewById(R.id.rcv_artistsList);
        LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcvArtists.setLayoutManager(linearLayoutManager1);
        artistItemAdapter = new ArtistItemAdapter(this.getContext(),mListSinger);///
        rcvArtists.setAdapter(artistItemAdapter);
        EventChangeSingerListener();

        //Searching display
        rcvRecom = view.findViewById(R.id.rcv_search_Recommendation);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        rcvRecom.setLayoutManager(linearLayoutManager2);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        rcvRecom.addItemDecoration(itemDecoration);

        editText = view.findViewById(R.id.search_editText);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().trim().equals("")){
                    rcvRecom.setVisibility(View.GONE);
                }else{
                    rcvRecom.setVisibility(View.VISIBLE);
                    //recommendItemAdapter = new RecommendItemAdapter(getRecomData2(editable.toString().trim()));
                    //rcvRecom.setAdapter(recommendItemAdapter);

                    EventChangeRecomListener(editable.toString());
                }
            }
        });
    }

    private void EventChangeRecomListener(String input){
        mListRecom.clear();
        db.collection("songs")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                                Song temp_song = dc.getDocument().toObject(Song.class);
                                String temp_artist = null;
                                for (String temp: temp_song.getRelatedSingers()
                                     ) {
                                    temp_artist += temp;
                                }
                                RecommendItem temp = new RecommendItem(0, temp_song.getName(), temp_artist, temp_song);
                                mListRecom.add(temp);
                            }
                        }

                        if(mListRecom.size() != 0){
//                            for (RecommendItem a: mListRecom
//                            ) {
//                                Log.e("Name: ", a.getTitle());
//                                Log.e("Obj Name: ", a.getSong().getName());
//                            }

                            if(FilterRecomItem(input)){
                                recommendItemAdapter = new RecommendItemAdapter(getContext(), mListRecom);
                                rcvRecom.setAdapter(recommendItemAdapter);
                                recommendItemAdapter.notifyDataSetChanged();
                            }else{
                                rcvRecom.setVisibility(View.GONE);
                            }
                        }
                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private boolean FilterRecomItem(String input){
        List<RecommendItem> temp_list = new ArrayList<>();
        int max_recom = 0;
        for (RecommendItem item : mListRecom) {
            String tmp_string = item.getSong().getName().toLowerCase();
            if(tmp_string.contains(input)){
                temp_list.add(item);
                max_recom ++;
                if(max_recom > 6){
                    break;
                }
            }
        }
        if(temp_list.isEmpty()){
            return false;
        }else{
            mListRecom = temp_list;
            return true;
        }
    }
    private void EventChangeSingerListener() {
        db.collection("singers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            }
                        }
                        artistItemAdapter.notifyDataSetChanged();
                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void EvenChangeAlbumListener() {
        db.collection("albums")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
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
                            }
                        }
                        albumItemAdapter.notifyDataSetChanged();
                        if (progressBar.getVisibility() == View.VISIBLE)
                            progressBar.setVisibility(View.GONE);
                    }
                });
    }

    private List<RecommendItem> getRecomData2(String inputString){
        List<RecommendItem> list =  getRecomData();
        List<RecommendItem> newList = new ArrayList<>();
        for(RecommendItem i : list){
            if(i.getTitle().equals(inputString)){
                newList.add(i);
            }
        }
        return newList;
    }

    private List<RecommendItem> getRecomData() {
        List<RecommendItem> list = new ArrayList<>();

        list.add(new RecommendItem(R.drawable.img,"A", "a"));
        list.add(new RecommendItem(R.drawable.img,"B", "b"));
        list.add(new RecommendItem(R.drawable.img,"C", "c"));
        list.add(new RecommendItem(R.drawable.img,"C", "c"));
        list.add(new RecommendItem(R.drawable.img,"C", "c"));

        return list;
    }
}