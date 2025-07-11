package com.example.musicapplication;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import java.io.IOException;
import java.util.Arrays;

public class MediaPlayerManager {

    private static MediaPlayerManager instance;
    private MediaPlayer mediaPlayer;
    private String[] allSongs;
    private int currentSongIndex = 0;
    private boolean isPrepared = false;

    private MediaPlayer.OnCompletionListener completionListener;

    private MediaPlayerManager() {
        mediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerManager getInstance() {
        if (instance == null) {
            instance = new MediaPlayerManager();
        }
        return instance;
    }

    public void setSongList(String[] songs) {
        this.allSongs = songs;
    }

    public void setCurrentSongIndex(int index) {
        currentSongIndex = index;
    }

    public int getCurrentSongIndex() {
        return currentSongIndex;
    }

    public String getCurrentSong() {
        return allSongs != null && currentSongIndex < allSongs.length ? allSongs[currentSongIndex] : null;
    }

    public void prepareSong(Context context, int index) {
        try {
            if (allSongs == null || index < 0 || index >= allSongs.length) return;

            String songFileName = allSongs[index];
            currentSongIndex = index;

            AssetFileDescriptor afd = context.getAssets().openFd("songs/" + songFileName + ".mp3");
            mediaPlayer.reset();
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            isPrepared = true;

            if (completionListener != null) {
                mediaPlayer.setOnCompletionListener(completionListener);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (isPrepared && mediaPlayer != null) mediaPlayer.start();
    }

    public void pause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) mediaPlayer.pause();
    }

    public void togglePlayPause() {
        if (isPlaying()) pause(); else start();
    }

    public void seekTo(int pos) {
        if (mediaPlayer != null) mediaPlayer.seekTo(pos);
    }

    public boolean isPlaying() {
        return mediaPlayer != null && mediaPlayer.isPlaying();
    }

    public int getCurrentPosition() {
        return mediaPlayer != null ? mediaPlayer.getCurrentPosition() : 0;
    }

    public int getDuration() {
        return mediaPlayer != null ? mediaPlayer.getDuration() : 0;
    }

    public void setOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        this.completionListener = listener;
        if (mediaPlayer != null) {
            mediaPlayer.setOnCompletionListener(listener);
        }
    }

    public void playNext(Context context) {
        if (allSongs == null || allSongs.length == 0) return;
        currentSongIndex = (currentSongIndex + 1) % allSongs.length;
        prepareSong(context, currentSongIndex);
        start();
    }

    public void playPrevious(Context context) {
        if (allSongs == null || allSongs.length == 0) return;
        currentSongIndex = (currentSongIndex - 1 + allSongs.length) % allSongs.length;
        prepareSong(context, currentSongIndex);
        start();
    }
}
