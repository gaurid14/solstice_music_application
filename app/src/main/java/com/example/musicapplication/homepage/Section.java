package com.example.musicapplication.homepage;

import java.util.List;

public class Section {
    private String title;
    private List<Album> albumList;

    public Section(String title, List<Album> albumList) {
        this.title = title;
        this.albumList = albumList;
    }

    public String getTitle() {
        return title;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }
}
