package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.UpdateSongActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.SongViewHolder> {
    private ArrayList<Song> mListSong;
    private ArrayList<String> mListSongId;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setFilteredListSong(ArrayList<Song> filteredListSong, ArrayList<String> filteredListSongId) {
        this.mListSong = filteredListSong;
        this.mListSongId = filteredListSongId;
        notifyDataSetChanged();
    }

    public AdminSongAdapter(Context context, ArrayList<Song> mListSong, ArrayList<String> mListSongId) {
        this.mListSong = mListSong;
        this.context = context;
        this.mListSongId = mListSongId;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin, parent,false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Song song = mListSong.get(position);
        if (song == null) {
            return;
        }

        holder.songName.setText(song.getName());
        StringBuilder relatedSingers = new StringBuilder();
        relatedSingers.append("");
        final boolean[] firstSinger = {true};

        List<String> getListSingerRelate = song.getRelatedSingers();
        for (String SingerID : getListSingerRelate) {
            readSongData(new FireStoreCallbackSinger() {
                @Override
                public void onCallback(Singer singer) {
                    if(firstSinger[0]) {
                        relatedSingers.append(singer.getName());
                        firstSinger[0] = false;
                    } else {
                        relatedSingers.append(", "+singer.getName());
                    }
                    if (relatedSingers.equals("")) holder.songSinger.setText("");
                    else holder.songSinger.setText(relatedSingers);
                }
            }, SingerID);
        }

        Glide.with(holder.songImg.getContext())
                .load(song.getImage())
                .placeholder(R.drawable.alec_album)
                .error(R.drawable.alec_album)
                .into(holder.songImg);

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateSongActivity.class);
                intent.putExtra("SongId", mListSongId.get(position));
                intent.putExtra("SongInfo", mListSong.get(position));
                context.startActivity(intent);
            }
        });

        holder.btn_deleteSong.setOnClickListener(view -> {
            deleteSongInAlbums(position);
            deleteSongInSingers(position);
            deleteSong(position);
            deleteSongImage(position);
            deleteSongInUser(position);
        });
    }

    private void deleteSongImage(int position) {
        StorageReference storageReference = storage.getReferenceFromUrl(mListSong.get(position).getImage());
        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.e("Success Storage delete: ", "Delete image successfully");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("Failure Storage delete: ", "Delete image fail");
            }
        });
    }

    private void deleteSongInUser(int position) {
        db.collection("users").document(auth.getUid())
                .update("favoriteSongs", FieldValue.arrayRemove(mListSongId.get(position)));
    }

    private void deleteSong(int position) {
        db.collection("songs").document(mListSongId.get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mListSong.remove(mListSong.get(position));
                        mListSongId.remove(mListSongId.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(context, "Delete Song Successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSongInSingers(int position) {
        db.collection("singers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference albumReference = db.collection("singers").document(document.getId());
                                albumReference.update("relatedSongs", FieldValue.arrayRemove(mListSongId.get(position)));
                            }
                        } else {
                            Log.e("Error", "Error getting documents: "+ task.getException());
                        }
                    }
                });
    }

    private void deleteSongInAlbums(int position) {
        db.collection("albums")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference albumReference = db.collection("albums").document(document.getId());
                                albumReference.update("relatedSongs", FieldValue.arrayRemove(mListSongId.get(position)));
                            }
                        } else {
                            Log.e("Error", "Error getting documents: "+ task.getException());
                        }
                    }
                });
    }

    private void readSongData(FireStoreCallbackSinger fireStoreCallback, String SingerID) {
        db.collection("singers").document(SingerID)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.w("FireStore Error", "Listen failed.", error);
                            return;
                        }
                        if (value != null && value.exists()) {
                            fireStoreCallback.onCallback(value.toObject(Singer.class));
                        } else {
                            Log.d("Null document: ", "Current data: null");
                        }
                    }
                });
    }

    private interface FireStoreCallbackSinger {
        void onCallback(Singer singer);
    }

    @Override
    public int getItemCount() {
        if (mListSong != null) {
            return mListSong.size();
        }
        return 0;
    }

    public class SongViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
        private ImageView songImg;
        private TextView songName;
        private TextView songSinger;
        private FrameLayout itemMusic;
        private Button btn_deleteSong;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImg = itemView.findViewById(R.id.song_img);
            songName = itemView.findViewById(R.id.song_name);
            songSinger = itemView.findViewById(R.id.song_Singer);
            //btnMore.setOnClickListener(this);
            itemMusic = itemView.findViewById(R.id.itemMusic);
            btn_deleteSong = itemView.findViewById(R.id.btn_deleteSong);
        }
    }
}
