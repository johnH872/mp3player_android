package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Singer implements Serializable {
    private String name;
    private String image;
    private String wallpaper;
    private ArrayList<String> relatedSongs;

    public Singer(String name, String image, ArrayList<String> relatedSongs, String wallpaper) {
        this.name = name;
        this.image = image;
        this.relatedSongs = relatedSongs;
        this.wallpaper = wallpaper;
    }

    public Singer() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<String> getRelatedSongs() {
        return relatedSongs;
    }

    public void setRelatedSongs(ArrayList<String> relatedSongs) {
        this.relatedSongs = relatedSongs;
    }

    public String getWallpaper() {
        return wallpaper;
    }

    public void setWallpaper(String wallpaper) {
        this.wallpaper = wallpaper;
    }
}
