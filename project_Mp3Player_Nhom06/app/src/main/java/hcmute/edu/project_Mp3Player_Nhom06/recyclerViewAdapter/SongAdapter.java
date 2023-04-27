package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


import hcmute.edu.project_Mp3Player_Nhom06.PlayMusicActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.User;
import hcmute.edu.project_Mp3Player_Nhom06.service.PlayMusicService;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {
    private static final int REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 1;
    private ArrayList<Song> mListSong;
    private ArrayList<String> mListSongId;
    Context context;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    Activity contexA;
    public SongAdapter(Context context, ArrayList<Song> mListSong, ArrayList<String> mListSongId, Activity contextA) {
        this.mListSong = mListSong;
        this.context = context;
        this.mListSongId = mListSongId;
        this.contexA = contextA;
    }

    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_music, parent,false);
        return new SongViewHolder(view).linkAdapter(this);
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
        for (String singerID : getListSingerRelate) {
            readSingerData(new FireStoreCallbackSinger() {
                @Override
                public void onCallback(Singer singer) {
                    if(firstSinger[0]) {
                        relatedSingers.append(singer.getName());
                        firstSinger[0] = false;
                    } else {
                        relatedSingers.append(", "+singer.getName());
                    }
                    holder.songSinger.setText(relatedSingers);
                }
            }, singerID);
        }

        readUserData(new FireStoreCallbackUser() {
            @Override
            public void onCallback(User user) {
                if (user.getFavoriteSongs().isEmpty())
                    return;
                String songIdTemp = mListSongId.get(position);
                for (String songId: user.getFavoriteSongs()) {
                    if (songIdTemp.equals(songId)) {
                        holder.favoriteButton.setIconResource(R.drawable.favorite_filled_48px);
                        return;
                    }
                }
            }
        });

        if(song.getImage().trim() != ""){
            Glide.with(holder.songImg.getContext())
                    .load(song.getImage())
                    .placeholder(R.drawable.alec_album)
                    .error(R.drawable.alec_album)
                    .into(holder.songImg);
        }else{
            holder.songImg.setImageResource(R.drawable.alec_album);
        }
        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkUserPermission(song);
            }
        });

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Stop current service
                Intent stopServiceIntent = new Intent(context, PlayMusicService.class);
                context.stopService(stopServiceIntent);
                //Play song
                Intent intent = new Intent(context, PlayMusicActivity.class);

                intent.putExtra("mListSong", mListSong);
                intent.putExtra("indexSong", holder.getAbsoluteAdapterPosition());

                context.startActivity(intent);
            }
        });
    }

    private void checkUserPermission(Song song) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ///Toast.makeText(context,"Something wrong ::))", Toast.LENGTH_LONG).show();
            startDownloadFile(song);
        }else{
            startDownloadFile(song);
        }
    }

    private void startDownloadFile(Song song) {
        String externalStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();

        String fileName = externalStoragePath + File.separator + song.getName()+ ".mp3";
        File localFile = new File(fileName);
        StorageReference storageRef = FirebaseStorage.getInstance().getReferenceFromUrl(song.getMusicResource().toString());

        storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(context, "Download success!!!",
                        Toast.LENGTH_LONG).show();
                //openNoticeDialog("Download OK");
                Log.d("file","download file " + fileName);
                Log.d("Location", localFile.toString());
//                D/file: download file /storage/emulated/0/3107-3.mp3
//                D/Location: /storage/emulated/0/3107-3.mp3
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("file","download fail");
            }
        });
    }

    public void release() {
        context = null;
    }

    private void readSingerData(FireStoreCallbackSinger fireStoreCallback, String singerID) {
        db.collection("singers").document(singerID)
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

    private void readUserData(FireStoreCallbackUser fireStoreCallbackUser) {
        db.collection("users").document(auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        fireStoreCallbackUser.onCallback(documentSnapshot.toObject(User.class));
                    }
                });
    }
    private interface FireStoreCallbackSinger {
        void onCallback(Singer singer);
    }

    private interface FireStoreCallbackUser {
        void onCallback(User user);
    }

    private interface FireStoreCallbackSong {
        void onCallback(Song song);
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
        private MaterialButton favoriteButton;

        private Button downloadButton;
        //private Button btnMore;
        private FrameLayout itemMusic;
        private SongAdapter songAdapter;
        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songImg = itemView.findViewById(R.id.song_img);
            songName = itemView.findViewById(R.id.song_name);
            songSinger = itemView.findViewById(R.id.song_singer);
            //btnMore.setOnClickListener(this);
            downloadButton = itemView.findViewById(R.id.btn_download);
            itemMusic = itemView.findViewById(R.id.itemMusic);
            favoriteButton = itemView.findViewById(R.id.btn_love);
            favoriteButton.setOnClickListener(view -> {
                readUserData(new FireStoreCallbackUser() {
                    @Override
                    public void onCallback(User user) {
                        boolean checkLiked = false;
                        DocumentReference songReference = db.collection("users").document(auth.getUid());
                        String songIdTemp = mListSongId.get(getBindingAdapterPosition());
                        ArrayList<String> favoriteSongs = user.getFavoriteSongs();
                        for (String songId: favoriteSongs) {
                            if (songId.equals(songIdTemp)) checkLiked = true;
                        }
                        if (checkLiked){
                            songReference.update("favoriteSongs", FieldValue.arrayRemove(songIdTemp));
                            Toast.makeText(context, "Removed from favorite", Toast.LENGTH_SHORT).show();
                            favoriteButton.setIconResource(R.drawable.favorite_48px);
                        }
                        else {
                            songReference.update("favoriteSongs", FieldValue.arrayUnion(songIdTemp));
                            Toast.makeText(context, "Saved to favorite", Toast.LENGTH_SHORT).show();
                            favoriteButton.setIconResource(R.drawable.favorite_filled_48px);
                        }
                        notifyDataSetChanged();
                    }
                });
            });
        }

        public SongViewHolder linkAdapter (SongAdapter songAdapter) {
            this.songAdapter = songAdapter;
            return this;
        }


//        @Override
//        public void onClick(View view) {
//            showPopupMenu(view);
//        }
//
//        private void showPopupMenu(View view) {
//            PopupMenu popupMenu = new PopupMenu(view.getContext(), view);
//            popupMenu.inflate(R.menu.top_app_bar_music);
//            popupMenu.setOnMenuItemClickListener(this);
//            popupMenu.show();
//        }
//
//        @Override
//        public boolean onMenuItemClick(MenuItem menuItem) {
//            Log.e("Hello", "Hello " + getAdapterPosition());
//            return true;
//        }
    }


}
