package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import hcmute.edu.project_Mp3Player_Nhom06.PlayMusicActivity;
import hcmute.edu.project_Mp3Player_Nhom06.R;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.RecommendItem;
import hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song;
import hcmute.edu.project_Mp3Player_Nhom06.service.PlayMusicService;

public class RecommendItemAdapter extends RecyclerView.Adapter<RecommendItemAdapter.RecommendItemViewHolder> {

    private List<RecommendItem> recommendItemList;

    private Context context;
    private ArrayList<Song> mListSong = new ArrayList<Song>();

    public RecommendItemAdapter(Context context, List<RecommendItem> recommendItemList) {
        this.recommendItemList = recommendItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public RecommendItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommendation_search_layout, parent, false);
        return new RecommendItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecommendItemViewHolder holder, int position) {
        RecommendItem item = recommendItemList.get(position);
        if(item == null){
            return;
        }
        mListSong.add(item.getSong());
        Glide.with(holder.imageView.getContext())
                .load(item.getSong().getImage())
                .placeholder(R.drawable.alec_album)
                .error(com.firebase.ui.database.R.drawable.common_google_signin_btn_icon_dark_focused)
                .into(holder.imageView);

        holder.imageView.setImageResource(item.getRecomImg());
        holder.title.setText(item.getTitle());
        holder.subTitle.setText(item.getSubTitle());

        holder.self.setOnClickListener(view -> RecomSelect(item));
    }

    private void RecomSelect(RecommendItem recommendItem){
        //Stop current service
        Intent stopServiceIntent = new Intent(context, PlayMusicService.class);
        context.stopService(stopServiceIntent);
        //Play song
        Intent intent = new Intent(context, PlayMusicActivity.class);
        intent.putExtra("mListSong", mListSong);
        Bundle bundle = new Bundle();
        bundle.putSerializable("mSong",recommendItem.getSong());
        bundle.putInt("indexSong", 0);
        intent.putExtras(bundle);

        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        if(recommendItemList != null){
            return recommendItemList.size();
        }
        return 0;
    }

    public class RecommendItemViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private TextView title, subTitle;

        private FrameLayout self;

        public RecommendItemViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.song_img);
            title = itemView.findViewById(R.id.song_name);
            subTitle = itemView.findViewById(R.id.song_singer);

            self = itemView.findViewById(R.id.recomSong);
        }
    }
}
