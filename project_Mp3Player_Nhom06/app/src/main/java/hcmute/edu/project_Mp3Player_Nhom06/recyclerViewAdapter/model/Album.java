package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Album implements Serializable {
    private String name;
    private String image;
    private ArrayList<String> relatedSongs;
    private String description;

    public Album() {
    }

    public Album(String name, String image, ArrayList<String> relatedSongs, String description) {
        this.name = name;
        this.image = image;
        this.relatedSongs = relatedSongs;
        this.description = description;
    }

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
