package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.AlbumActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;
import hcmute.edu.project_Mp3Player_Nhom06.R;

public class AlbumItemAdapter extends RecyclerView.Adapter<AlbumItemAdapter.AlbumItemViewHolder> {

    private List<Album> listAlbum;
    private Context mContext;

    public AlbumItemAdapter(Context context, List<Album> listAlbum) {
        this.listAlbum = listAlbum;
        this.mContext = context;
    }

    @NonNull
    @Override
    public AlbumItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_card_layout, parent, false);
        return new AlbumItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumItemViewHolder holder, int position) {
        Album album = listAlbum.get(position);
        if(album == null){
            return;
        }

        Glide.with(holder.cardImg.getContext())
                .load(album.getImage())
                .placeholder(R.drawable.alec_album)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_focused)
                .into(holder.cardImg);

        holder.cardTitle.setText(album.getName());
        holder.cardSubTitle.setText(album.getDescription());

        holder.cardItem.setOnClickListener(view -> onClickGotoAlbum(album));
    }

    private void onClickGotoAlbum(Album item) {
        Intent intent = new Intent(mContext, AlbumActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_album", item);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if(listAlbum != null){
            return listAlbum.size();
        }
        return 0;
    }

    public class AlbumItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView cardImg;
        private TextView cardTitle, cardSubTitle;
        private MaterialCardView cardItem;

        public AlbumItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImg = itemView.findViewById(R.id.card_image);
            cardTitle = itemView.findViewById(R.id.card_title);
            cardSubTitle = itemView.findViewById(R.id.card_subTitle);

            cardItem = itemView.findViewById(R.id.roundCardView);

        }
    }
}
