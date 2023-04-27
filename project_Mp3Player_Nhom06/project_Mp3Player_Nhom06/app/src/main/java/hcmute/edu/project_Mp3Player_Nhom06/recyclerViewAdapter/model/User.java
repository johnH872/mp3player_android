package hcmute.edu.project_Mp3Player_Nhom06.recyclerViewAdapter.model;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class User {
    private String name;
    private Timestamp birth;
    private ArrayList<String> favoriteSongs;

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getBirth() {
        return birth;
    }

    public void setBirth(Timestamp birth) {
        this.birth = birth;
    }

    public ArrayList<String> getFavoriteSongs() {
        return favoriteSongs;
    }

    public void setFavoriteSongs(ArrayList<String> favoriteSongs) {
        this.favoriteSongs = favoriteSongs;
    }
}
