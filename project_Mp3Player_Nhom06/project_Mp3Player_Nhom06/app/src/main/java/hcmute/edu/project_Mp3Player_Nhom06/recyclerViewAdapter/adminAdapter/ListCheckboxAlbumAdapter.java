package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.adminAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;

public class ListCheckboxAlbumAdapter extends RecyclerView.Adapter<ListCheckboxAlbumAdapter.ListCheckboxAlbumAdapterViewHolder> {
    private ArrayList<Album> mListAlbum;
    private ArrayList<String> mListAlbumId;
    HashMap<String, Album> mListAlbumAdd;
    Context context;
    public interface OnItemCheckListener {
        void onItemCheck(Album Album, String id);
        void onItemUnCheck(Album Album, String id);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void setFilteredListAlbum(ArrayList<Album> filteredListAlbum, ArrayList<String> filteredListAlbumId) {
        this.mListAlbum = filteredListAlbum;
        this.mListAlbumId = filteredListAlbumId;
        notifyDataSetChanged();
    }

    public ListCheckboxAlbumAdapter(ArrayList<Album> mListAlbum, ArrayList<String> mListAlbumId,
                                    HashMap<String, Album> mListAlbumAdd, Context context, OnItemCheckListener onItemCheckListener) {
        this.mListAlbum = mListAlbum;
        this.mListAlbumId = mListAlbumId;
        this.context = context;
        this.mListAlbumAdd = mListAlbumAdd;
        this.onItemClick = onItemCheckListener;
    }

    @NonNull
    @Override
    public ListCheckboxAlbumAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_list, parent,false);
        return new ListCheckboxAlbumAdapterViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ListCheckboxAlbumAdapterViewHolder holder, int position) {
        if (holder instanceof ListCheckboxAlbumAdapterViewHolder) {
            Album Album = mListAlbum.get(position);
            if (Album == null) return;

            holder.AlbumName.setText(Album.getName());

            Glide.with(holder.AlbumImage.getContext())
                    .load(Album.getImage())
                    .placeholder(R.drawable.alec_album)
                    .error(R.drawable.alec_album)
                    .into(holder.AlbumImage);

            ((ListCheckboxAlbumAdapterViewHolder) holder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListCheckboxAlbumAdapterViewHolder) holder).checkBox.setChecked(
                            !((ListCheckboxAlbumAdapterViewHolder) holder).checkBox.isChecked());
                    if (((ListCheckboxAlbumAdapterViewHolder) holder).checkBox.isChecked()) {
                        onItemClick.onItemCheck(Album, mListAlbumId.get(position));
                    } else {
                        onItemClick.onItemUnCheck(Album, mListAlbumId.get(position));
                    }
                }
            });

            for (Map.Entry<String, Album> entry: mListAlbumAdd.entrySet()) {
                if (entry.getKey().equals(mListAlbumId.get(position))) {
                    ((ListCheckboxAlbumAdapterViewHolder) holder).checkBox.setChecked(true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mListAlbum != null) {
            return mListAlbum.size();
        }
        return 0;
    }

    public class ListCheckboxAlbumAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView AlbumImage;
        TextView AlbumName;
        CheckBox checkBox;
        View itemView;
        public ListCheckboxAlbumAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            AlbumImage = itemView.findViewById(R.id.song_img);
            AlbumName = itemView.findViewById(R.id.txtV_name);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
