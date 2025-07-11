//package com.example.musicapplication;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.widget.ImageView;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.annotation.RequiresApi;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.appcompat.widget.AppCompatImageButton;
//
//import com.example.musicapplication.database.CSVHandler;
//import com.example.musicapplication.database.DBHandler;
//
//import org.json.JSONArray;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//public class MediaPlayerActivity extends AppCompatActivity {
//
//    private AppCompatImageButton next, previous, playPause;
//    private ImageView musicImage;
//    private TextView songname, startTime, totaltime;
//    private SeekBar progress;
//
//    private DBHandler dbHandler;
//    private SharedPreferences sharedPreferences;
//
//    private Handler handle = new Handler();
//    private static final String SHARED_PREFS = "shared_prefs";
//    private static final String USERNAME = "user_name";
//    private static int o_Time = 0, s_Time = 0, e_Time = 0;
//
//    private static int currentSong = 0;
//    private static int count = 0;
//    private static int num = 0;
//
//    private static String click_song_name;
//    private static String strUsername;
//
//    private static List<String> recommendations = new ArrayList<>();
//    private static String[] nextSongs;
//    public static String[] allSongs;
//
//    private final MediaPlayerManager playerManager = MediaPlayerManager.getInstance();
//
//    private Runnable UpdateSongTime = new Runnable() {
//        @Override
//        public void run() {
//            s_Time = playerManager.getCurrentPosition();
//            startTime.setText(formatTime(s_Time));
//            progress.setProgress(s_Time);
//            handle.postDelayed(this, 100);
//        }
//    };
//
//    private void assignSong(int index) {
//        try {
//            String song = allSongs[index];
//            playerManager.prepareSong(this, song);
//            songname.setText(song.replace("_", " "));
//            int imageId = getResources().getIdentifier(song, "drawable", getPackageName());
//            musicImage.setImageResource(imageId);
//
//            dbHandler.userSongData(strUsername, song, ++count);
//            dbHandler.checkUserSongData();
//
//            playerManager.setOnCompletionListener(mp -> playNext());
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(this, "Error loading song", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_media_player);
//
//        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
//        strUsername = sharedPreferences.getString(USERNAME, null);
//
//        dbHandler = new DBHandler(this);
//
//        CSVHandler csvReader = new CSVHandler(getApplicationContext());
//        allSongs = csvReader.getColumnData(1);
//
//        Intent intent = getIntent();
//        click_song_name = intent.getStringExtra("songName");
//        currentSong = Arrays.asList(allSongs).indexOf(click_song_name);
//
//        initializeViews();
//        thread.start();  // Start fetching recommendations
//
//        assignSong(currentSong);
//        playerManager.start();
//        playPause.setImageResource(R.drawable.ic_baseline_pause_24);
//        getTime();
//
//        setListeners();
//    }
//
//    private void initializeViews() {
//        musicImage = findViewById(R.id.songcover);
//        previous = findViewById(R.id.prev);
//        next = findViewById(R.id.next);
//        playPause = findViewById(R.id.playPause);
//        songname = findViewById(R.id.songname);
//        startTime = findViewById(R.id.time);
//        totaltime = findViewById(R.id.totaltime);
//        progress = findViewById(R.id.seekbar);
//    }
//
//    private void setListeners() {
//        playPause.setOnClickListener(v -> {
//            if (playerManager.isPlaying()) {
//                playerManager.pause();
//                playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
//            } else {
//                playerManager.start();
//                playPause.setImageResource(R.drawable.ic_baseline_pause_24);
//            }
//            getTime();
//        });
//
//        next.setOnClickListener(v -> playNext());
//
//        previous.setOnClickListener(v -> {
//            currentSong = (currentSong - 1 + allSongs.length) % allSongs.length;
//            assignSong(currentSong);
//            playerManager.start();
//            playPause.setImageResource(R.drawable.ic_baseline_pause_24);
//            getTime();
//        });
//
//        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}
//            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
//            @Override public void onStopTrackingTouch(SeekBar seekBar) {
//                playerManager.seekTo(seekBar.getProgress());
//            }
//        });
//    }
//
//    private void playNext() {
//        if (nextSongs != null && num < nextSongs.length) {
//            String nextSong = nextSongs[num++];
//            currentSong = Arrays.asList(allSongs).indexOf(nextSong);
//            assignSong(currentSong);
//            playerManager.start();
//            playPause.setImageResource(R.drawable.ic_baseline_pause_24);
//            getTime();
//        } else {
//            Toast.makeText(this, "No more recommended songs", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void getTime() {
//        o_Time = 0;
//        e_Time = playerManager.getDuration();
//        s_Time = playerManager.getCurrentPosition();
//
//        if (o_Time == 0) {
//            progress.setMax(e_Time);
//            o_Time = 1;
//        }
//
//        totaltime.setText(formatTime(e_Time));
//        startTime.setText(formatTime(s_Time));
//        progress.setProgress(s_Time);
//
//        handle.postDelayed(UpdateSongTime, 100);
//    }
//
//    private String formatTime(int millis) {
//        return String.format("%d:%02d",
//                TimeUnit.MILLISECONDS.toMinutes(millis),
//                TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
//    }
//
//    Thread thread = new Thread(() -> {
//        try {
//            String baseUrl = "http://192.168.90.176:5000/get_recommendations?song=songname&recs=50";
//            String formattedUrl = baseUrl.replace("songname", click_song_name);
//            URL url = new URL(formattedUrl);
//
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("Accept", "application/json");
//
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            StringBuilder response = new StringBuilder();
//            String line;
//
//            while ((line = reader.readLine()) != null) {
//                response.append(line);
//            }
//
//            JSONObject json = new JSONObject(response.toString());
//            if (json.has("recomms")) {
//                JSONArray recomms = json.getJSONArray("recomms");
//                for (int i = 0; i < recomms.length(); i++) {
//                    recommendations.add(recomms.getString(i));
//                }
//                nextSongs = recommendations.toArray(new String[0]);
//                num = 0;
//                recommendations.clear();
//            }
//
//            reader.close();
//            conn.disconnect();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    });
//}
