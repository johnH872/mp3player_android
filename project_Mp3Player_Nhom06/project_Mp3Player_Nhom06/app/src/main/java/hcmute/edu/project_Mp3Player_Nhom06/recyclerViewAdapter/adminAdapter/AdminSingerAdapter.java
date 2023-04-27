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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.adminActivity.UpdateSingerActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;

public class AdminSingerAdapter extends RecyclerView.Adapter<AdminSingerAdapter.SingerViewHolder> {
    private ArrayList<Singer> mListSinger;
    private ArrayList<String> mListSingerId;
    Context context;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    public void setFilteredListSinger(ArrayList<Singer> filteredListSinger, ArrayList<String> filteredListSingerId) {
        this.mListSinger = filteredListSinger;
        this.mListSingerId = filteredListSingerId;
        notifyDataSetChanged();
    }

    public AdminSingerAdapter(Context context, ArrayList<Singer> mListSinger, ArrayList<String> mListSingerId) {
        this.mListSinger = mListSinger;
        this.context = context;
        this.mListSingerId = mListSingerId;
    }

    @NonNull
    @Override
    public SingerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_admin, parent,false);
        return new SingerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SingerViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Singer Singer = mListSinger.get(position);
        if (Singer == null) {
            return;
        }

        holder.SingerName.setText(Singer.getName());

        Glide.with(holder.SingerImg.getContext())
                .load(Singer.getImage())
                .placeholder(R.drawable.alec_album)
                .error(R.drawable.alec_album)
                .into(holder.SingerImg);

        holder.itemMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, UpdateSingerActivity.class);
                intent.putExtra("SingerId", mListSingerId.get(position));
                intent.putExtra("SingerInfo", mListSinger.get(position));
                context.startActivity(intent);
            }
        });

        holder.btn_deleteSinger.setOnClickListener(view -> {
            deleteSingerImage(position);
            deleteSingerWallpaper(position);
            deleteSingerInSongs(position);
            deleteSinger(position);
        });
    }

    private void deleteSingerImage(int position) {
        StorageReference storageReference = storage.getReferenceFromUrl(mListSinger.get(position).getImage());
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

    private void deleteSingerWallpaper(int position) {
        StorageReference storageReference = storage.getReferenceFromUrl(mListSinger.get(position).getWallpaper());
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

    private void deleteSinger(int position) {
        db.collection("singers").document(mListSingerId.get(position))
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        mListSinger.remove(mListSinger.get(position));
                        mListSingerId.remove(mListSingerId.get(position));
                        notifyDataSetChanged();
                        Toast.makeText(context, "Delete Singer Successfully", Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteSingerInSongs(int position) {
        db.collection("songs")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference albumReference = db.collection("songs").document(document.getId());
                                albumReference.update("relatedSingers", FieldValue.arrayRemove(mListSingerId.get(position)));
                            }
                        } else {
                            Log.e("Error", "Error getting documents: "+ task.getException());
                        }
                    }
                });
    }

    @Override
    public int getItemCount() {
        if (mListSinger != null) {
            return mListSinger.size();
        }
        return 0;
    }

    public class SingerViewHolder extends RecyclerView.ViewHolder {
        //implements View.OnClickListener, PopupMenu.OnMenuItemClickListener
        private ImageView SingerImg;
        private TextView SingerName;
        private FrameLayout itemMusic;
        private Button btn_deleteSinger;
        public SingerViewHolder(@NonNull View itemView) {
            super(itemView);
            SingerImg = itemView.findViewById(R.id.song_img);
            SingerName = itemView.findViewById(R.id.song_name);
            //btnMore.setOnClickListener(this);
            itemMusic = itemView.findViewById(R.id.itemMusic);
            btn_deleteSinger = itemView.findViewById(R.id.btn_deleteSong);
        }
    }
}
