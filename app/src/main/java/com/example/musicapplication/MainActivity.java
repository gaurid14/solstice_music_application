package com.example.musicapplication;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.musicapplication.database.CSVHandler;
import com.example.musicapplication.homepage.HomeFragment;
import com.example.musicapplication.homepage.LibraryFragment;
import com.example.musicapplication.homepage.SearchFragment;
import com.example.musicapplication.homepage.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNav;
    LinearLayout miniPlayer;
    ImageButton btnPlayPause, btnNext, btnPrev;
    TextView miniTitle;
    ImageView miniArt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNav = findViewById(R.id.bottom_nav);
        miniPlayer = findViewById(R.id.miniPlayer);
        btnPlayPause = findViewById(R.id.miniPlayPause);
//        btnNext = findViewById(R.id.miniNext);
//        btnPrev = findViewById(R.id.miniPrev);
        miniTitle = findViewById(R.id.miniTitle);
        miniArt = findViewById(R.id.miniArt);

        CSVHandler csvReader = new CSVHandler(getApplicationContext());
        MediaPlayerActivity.allSongs = csvReader.getColumnData(1);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();

        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_search) {
                selectedFragment = new SearchFragment();
            } else if (itemId == R.id.nav_library) {
                selectedFragment = new LibraryFragment();
            } else if (itemId == R.id.nav_settings) {
                selectedFragment = new SettingsFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
                return true;
            }
            return false;
        });

//        miniPlayer.setVisibility(View.VISIBLE);

        miniPlayer.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MediaPlayerActivity.class);
            intent.putExtra("songIndex", MediaPlayerActivity.currentSong);
            intent.putExtra("songName", MediaPlayerActivity.allSongs[MediaPlayerActivity.currentSong]);
            startActivity(intent);
        });

        btnPlayPause.setOnClickListener(v -> {
            if (MediaPlayerActivity.mediaPlayer != null) {
                if (MediaPlayerActivity.mediaPlayer.isPlaying()) {
                    MediaPlayerActivity.mediaPlayer.pause();
                    btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                } else {
                    MediaPlayerActivity.mediaPlayer.start();
                    btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
                }
            }
        });

//        btnNext.setOnClickListener(v -> {
//            MediaPlayerActivity activity = MediaPlayerActivity.getInstance();
//            if (activity != null) {
//                activity.playNext();
//                updateMiniPlayerUI();
//            }
//        });

//        btnPrev.setOnClickListener(v -> {
//            MediaPlayerActivity activity = MediaPlayerActivity.getInstance();
//            if (activity != null) {
//                activity.runOnUiThread(() -> {
//                    MediaPlayerActivity.currentSong--;
//                    if (MediaPlayerActivity.currentSong < 0) {
//                        MediaPlayerActivity.currentSong = MediaPlayerActivity.allSongs.length - 1;
//                    }
//                    activity.assignSong(MediaPlayerActivity.currentSong);
//                    MediaPlayerActivity.mediaPlayer.start();
//                    updateMiniPlayerUI();
//                });
//            } else {
//                Log.e("MiniPlayer", "MediaPlayerActivity instance is null.");
//            }
//        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateMiniPlayerUI();
    }

//    private void updateMiniPlayerUI() {
//        if (MediaPlayerActivity.mediaPlayer != null && MediaPlayerActivity.allSongs != null) {
//            List<String> songs = Arrays.asList(MediaPlayerActivity.allSongs);
//            int current = MediaPlayerActivity.currentSong;
//
//            if (current >= 0 && current < songs.size()) {
//                miniTitle.setText(songs.get(current));
//                int imageRes = getResources().getIdentifier(songs.get(current), "drawable", getPackageName());
//                if (imageRes != 0) {
//                    miniArt.setImageResource(imageRes);
//                } else {
//                    miniArt.setImageResource(R.drawable.itunes_logo_png_transparent);
//                }
//            } else {
//                miniTitle.setText("Now Playing");
//            }
//
//            if (MediaPlayerActivity.mediaPlayer.isPlaying()) {
//                btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
//            } else {
//                btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//            }
//        } else {
//            miniTitle.setText("Now Playing");
//            btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//        }
//    }

    private void updateMiniPlayerUI() {
        if (MediaPlayerActivity.mediaPlayer != null &&
                MediaPlayerActivity.allSongs != null &&
                MediaPlayerActivity.isMediaPlayerInitialized &&
                MediaPlayerActivity.currentSong >= 0 &&
                MediaPlayerActivity.currentSong < MediaPlayerActivity.allSongs.length) {

            miniPlayer.setVisibility(View.VISIBLE);

            String currentSongName = MediaPlayerActivity.allSongs[MediaPlayerActivity.currentSong];
            miniTitle.setText(currentSongName);

            int imageRes = getResources().getIdentifier(currentSongName, "drawable", getPackageName());
            if (imageRes != 0) {
                miniArt.setImageResource(imageRes);
            } else {
                miniArt.setImageResource(R.drawable.itunes_logo_png_transparent);
            }

            if (MediaPlayerActivity.mediaPlayer.isPlaying()) {
                btnPlayPause.setImageResource(R.drawable.ic_baseline_pause_24);
            } else {
                btnPlayPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }

        } else {
            miniPlayer.setVisibility(View.GONE); // ✅ Hide if invalid
        }
    }
}
