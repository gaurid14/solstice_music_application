package com.example.musicapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;

public class NotificationActionReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MediaPlayerActivity player = MediaPlayerActivity.getInstance();

        if (player == null || MediaPlayerActivity.mediaPlayer == null) return;

        switch (action) {
            case "ACTION_PLAY_PAUSE":
                if (MediaPlayerActivity.mediaPlayer.isPlaying()) {
                    MediaPlayerActivity.mediaPlayer.pause();
                } else {
                    MediaPlayerActivity.mediaPlayer.start();
                }
                break;

            case "ACTION_PREV":
                MediaPlayerActivity.currentSong =
                        (MediaPlayerActivity.currentSong - 1 + MediaPlayerActivity.allSongs.length) % MediaPlayerActivity.allSongs.length;
                player.assignSong(MediaPlayerActivity.currentSong);
                MediaPlayerActivity.mediaPlayer.start();
                break;
        }

        // Update notification after action
        Intent serviceIntent = new Intent(context, NotificationService.class);
        ContextCompat.startForegroundService(context, serviceIntent);
    }
}

