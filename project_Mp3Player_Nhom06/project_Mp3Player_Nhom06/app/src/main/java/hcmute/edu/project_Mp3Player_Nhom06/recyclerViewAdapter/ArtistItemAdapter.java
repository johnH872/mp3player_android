package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.AlbumActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.SingerSongsActivity;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Album;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Singer;

public class ArtistItemAdapter extends RecyclerView.Adapter<ArtistItemAdapter.ArtistItemViewHolder>{
    private List<Singer> listArtist;
    private Context mContext;

    public ArtistItemAdapter(Context mContext, List<Singer> listArtist) {
        this.listArtist = listArtist;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ArtistItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.homepage_artist_card_layout, parent, false);
        return new ArtistItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ArtistItemViewHolder holder, int position) {
        Singer singer = listArtist.get(position);
        if(singer == null){
            return;
        }

        Glide.with(holder.cardImg.getContext())
                .load(singer.getImage())
                .placeholder(R.drawable.alec_album)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_focused)
                .into(holder.cardImg);

        holder.cardTitle.setText(singer.getName());
        holder.cardSubTitle.setText("Artist");

        holder.cardImg.setOnClickListener(view -> onClickGotoSinger(singer));
    }

    private void onClickGotoSinger(Singer item) {
        Intent intent = new Intent(mContext, SingerSongsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_singer", item);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if(listArtist != null){
            return listArtist.size();
        }
        return 0;
    }

    public class ArtistItemViewHolder extends RecyclerView.ViewHolder{
        private ShapeableImageView cardImg;
        private TextView cardTitle, cardSubTitle;

        public ArtistItemViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImg = itemView.findViewById(R.id.card_image);
            cardTitle = itemView.findViewById(R.id.card_title);
            cardSubTitle = itemView.findViewById(R.id.card_subTitle);
        }
    }
}
