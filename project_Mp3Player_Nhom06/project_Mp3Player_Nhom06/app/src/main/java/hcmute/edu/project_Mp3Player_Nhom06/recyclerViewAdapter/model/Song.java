package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model;

import android.net.Uri;

import java.io.Serializable;
import java.util.ArrayList;

public class Song implements Serializable {
    private String Image;
    private String name;
    private ArrayList<String> relatedSingers;
    private String musicResource;

    public Song() {

    }

    public Song(String image, String name, ArrayList<String> relatedSingers, String music) {
        Image = image;
        this.name = name;
        this.relatedSingers = relatedSingers;
        this.musicResource = music;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getRelatedSingers() {
        return relatedSingers;
    }

    public void setRelatedSingers(ArrayList<String> relatedSingers) {
        this.relatedSingers = relatedSingers;
    }

    public Uri getMusicResource() {
        return Uri.parse(musicResource);
    }

    public void setMusicResource(String musicResource) {
        this.musicResource = musicResource;
    }
}
