package com.example.musicapplication.homepage;
public class Album {
    private int imageResId;
    private String songName;

    public Album(int imageResId, String songName) {
        this.imageResId = imageResId;
        this.songName = songName;
    }

    public int getImageResId() {
        return imageResId;
    }

    public String getSongName() {
        return songName;
    }
}
