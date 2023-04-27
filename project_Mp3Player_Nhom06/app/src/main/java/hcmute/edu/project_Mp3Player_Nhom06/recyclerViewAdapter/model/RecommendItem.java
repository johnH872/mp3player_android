package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model;

public class RecommendItem {
    private int recomImg;
    private String Title, subTitle;

    private Song Song;

    public RecommendItem(int recomImg, String title, String subTitle) {
        this.recomImg = recomImg;
        Title = title;
        this.subTitle = subTitle;
    }

    public RecommendItem(int recomImg, String title, String subTitle, Song SongId) {
        this.recomImg = recomImg;
        Title = title;
        this.subTitle = subTitle;
        this.Song = SongId;
    }

    public int getRecomImg() {
        return recomImg;
    }

    public void setRecomImg(int recomImg) {
        this.recomImg = recomImg;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song getSong() {
        return Song;
    }

    public void setSong(hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model.Song song) {
        Song = song;
    }
}
