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
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;

public class ListCheckboxSingerAdapter extends RecyclerView.Adapter<ListCheckboxSingerAdapter.ListCheckboxSingerAdapterViewHolder> {
    private ArrayList<Singer> mListSinger;
    private ArrayList<String> mListSingerId;
    HashMap<String, Singer> mListSingerAdd;
    Context context;
    public interface OnItemCheckListener {
        void onItemCheck(Singer Singer, String id);
        void onItemUnCheck(Singer Singer, String id);
    }

    @NonNull
    private OnItemCheckListener onItemClick;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void setFilteredListSinger(ArrayList<Singer> filteredListSinger, ArrayList<String> filteredListSingerId) {
        this.mListSinger = filteredListSinger;
        this.mListSingerId = filteredListSingerId;
        notifyDataSetChanged();
    }

    public ListCheckboxSingerAdapter(ArrayList<Singer> mListSinger, ArrayList<String> mListSingerId,
                                     HashMap<String, Singer> mListSingerAdd, Context context, OnItemCheckListener onItemCheckListener) {
        this.mListSinger = mListSinger;
        this.mListSingerId = mListSingerId;
        this.context = context;
        this.mListSingerAdd = mListSingerAdd;
        this.onItemClick = onItemCheckListener;
    }

    @NonNull
    @Override
    public ListCheckboxSingerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_check_list, parent,false);
        return new ListCheckboxSingerAdapterViewHolder(view);
    }

    @SuppressLint("RecyclerView")
    @Override
    public void onBindViewHolder(@NonNull ListCheckboxSingerAdapterViewHolder holder, int position) {
        if (holder instanceof ListCheckboxSingerAdapterViewHolder) {
            Singer Singer = mListSinger.get(position);
            if (Singer == null) return;

            holder.SingerName.setText(Singer.getName());

            Glide.with(holder.SingerImage.getContext())
                    .load(Singer.getImage())
                    .placeholder(R.drawable.alec_album)
                    .error(R.drawable.alec_album)
                    .into(holder.SingerImage);

            ((ListCheckboxSingerAdapterViewHolder) holder).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ListCheckboxSingerAdapterViewHolder) holder).checkBox.setChecked(
                            !((ListCheckboxSingerAdapterViewHolder) holder).checkBox.isChecked());
                    if (((ListCheckboxSingerAdapterViewHolder) holder).checkBox.isChecked()) {
                        onItemClick.onItemCheck(Singer, mListSingerId.get(position));
                    } else {
                        onItemClick.onItemUnCheck(Singer, mListSingerId.get(position));
                    }
                }
            });

            for (Map.Entry<String, Singer> entry: mListSingerAdd.entrySet()) {
                if (entry.getKey().equals(mListSingerId.get(position))) {
                    ((ListCheckboxSingerAdapterViewHolder) holder).checkBox.setChecked(true);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mListSinger != null) {
            return mListSinger.size();
        }
        return 0;
    }

    public class ListCheckboxSingerAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView SingerImage;
        TextView SingerName;
        CheckBox checkBox;
        View itemView;
        public ListCheckboxSingerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            this.itemView = itemView;
            SingerImage = itemView.findViewById(R.id.song_img);
            SingerName = itemView.findViewById(R.id.txtV_name);
            checkBox = itemView.findViewById(R.id.checkBox);
            checkBox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            itemView.setOnClickListener(onClickListener);
        }
    }
}
