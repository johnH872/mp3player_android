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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.UpdateAlbumActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;

public class AdminAlbumAdapter extends RecyclerView.Adapter<AdminAlbumAdapter.AlbumViewHolder> {
    private ArrayList<Album> mListAlbum;
    private ArrayList<String> mListAlbumId;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setFilteredListAlbum(ArrayList<Album> filteredListAlbum, ArrayList<String> filteredListAlbumId) {
        this.mListAlbum = filteredListAlbum;
        this.mListAlbumId = filteredListAlbumId;
        notifyDataSetChanged();
    }

    public AdminAlbumAdapter(Context context, ArrayList<Album> mListAlbum, ArrayList<String> mListAlbumId) {
        this.mListAlbum = mListAlbum;
        this.context = context;
        this.mListAlbumId = mListAlbumId;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin, parent,false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Album Album = mListAlbum.get(position);
        if (Album == null) {
            return;
        }

        holder.AlbumName.setText(Album.getName());

        Glide.with(holder.AlbumImg.getContext())
                .load(Album.getImage())
                .placeholder(R.drawable.alec_album)
                .error(R.drawable.alec_album)
                .into(holder.AlbumImg);

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateAlbumActivity.class);
                intent.putExtra("AlbumId", mListAlbumId.get(position));
                intent.putExtra("AlbumInfo", mListAlbum.get(position));
                context.startActivity(intent);
            }
        });

        holder.btn_deleteAlbum.setOnClickListener(view -> {
            deleteAlbumImage(position);
            deleteAlbum(position);
        });
    }

    private void deleteAlbumImage(int position) {
        StorageReference storageReference = storage.getReferenceFromUrl(mListAlbum.get(position).getImage());
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

    private void deleteAlbum(int position) {
        db.collection("albums").document(mListAlbumId.get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mListAlbum.remove(mListAlbum.get(position));
                        mListAlbumId.remove(mListAlbumId.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(context, "Delete Album Successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mListAlbum != null) {
            return mListAlbum.size();
        }
        return 0;
    }

    public class AlbumViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
        private ImageView AlbumImg;
        private TextView AlbumName;
        private FrameLayout itemMusic;
        private Button btn_deleteAlbum;
        public AlbumViewHolder(@NonNull View itemView) {
            super(itemView);
            AlbumImg = itemView.findViewById(R.id.song_img);
            AlbumName = itemView.findViewById(R.id.song_name);
            //btnMore.setOnClickListener(this);
            itemMusic = itemView.findViewById(R.id.itemMusic);
            btn_deleteAlbum = itemView.findViewById(R.id.btn_deleteSong);
        }
    }
}
