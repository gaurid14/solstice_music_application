package com.example.musicapplication;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class NotificationService extends Service {

    public static final String CHANNEL_ID = "MusicPlayerChannel";
    public static final int NOTIFICATION_ID = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private NotificationManager notificationManager;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    public void showNotification(Context context, String songTitle, String artist, boolean isPlaying) {
        if (context == null) {
            Log.e("Notification", "Context is null – cannot show notification");
            return;
        }

        Intent intent = new Intent(context, MediaPlayerActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Play/Pause action
        Intent playPauseIntent = new Intent(this, NotificationActionReceiver.class);
        playPauseIntent.setAction("ACTION_PLAY_PAUSE");
        PendingIntent playPausePendingIntent = PendingIntent.getBroadcast(this, 0, playPauseIntent, PendingIntent.FLAG_IMMUTABLE);

        // Next action
        Intent nextIntent = new Intent(this, NotificationActionReceiver.class);
        nextIntent.setAction("ACTION_NEXT");
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(this, 1, nextIntent, PendingIntent.FLAG_IMMUTABLE);

        // Previous action
        Intent prevIntent = new Intent(this, NotificationActionReceiver.class);
        prevIntent.setAction("ACTION_PREV");
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(this, 2, prevIntent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(songTitle)
                .setContentText(artist)
                .setSmallIcon(R.drawable.itunes_logo_png_transparent)
                .setContentIntent(pendingIntent)
                .addAction(R.drawable.ic_baseline_skip_previous_24, "Prev", prevPendingIntent)
                .addAction(isPlaying ? R.drawable.ic_baseline_pause_24 : R.drawable.ic_baseline_play_arrow_24, "Play/Pause", playPausePendingIntent)
                .addAction(R.drawable.ic_baseline_skip_next_24, "Next", nextPendingIntent)
                .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                        .setShowActionsInCompactView(0, 1, 2))
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setOngoing(isPlaying)
                .setOnlyAlertOnce(true);

        startForeground(NOTIFICATION_ID, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Music Playback",
                    NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Music playback controls");
            notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}

