package com.example.musicapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.musicapplication.database.CSVHandler;
import com.example.musicapplication.database.DBHandler;
//import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MediaPlayerActivity extends AppCompatActivity {

    private static int o_Time = 0, s_Time = 0, e_Time = 0;
    private AppCompatImageButton next, previous, playPause;
    private Handler handle = new Handler();
    public static MediaPlayer mediaPlayer = new MediaPlayer();
    private AudioManager audioManager;
    private AudioAttributes audioAttributes;
    private ImageView musicImage, backgroundImage, arrowDown;
    private TextView songname;
    private TextView startTime;
    private TextView totaltime;
    private SeekBar progress;
    private DBHandler dbHandler;
    public static final String SHARED_PREFS = "shared_prefs";
    public static final String USERNAME = "user_name";
    String strUsername;
    SharedPreferences sharedPreferences;
    AssetFileDescriptor assetFileDescriptor;
    public static int currentSong = 0;
    public static int count = 0;
    Thread updateProgress;
    static String click_song_name;
    private static List<String> recommendations = new ArrayList<>();
    private static String[] nextSongs;
    private static int num = 0;
//    private SlidingUpPanelLayout slidingLayout;
    //    private  String[] allSongs = {"ajab_si","chandra","creepin","die_for_you","jaan_nisaar","kya_mujhe_pyaar","reminder","starboy","the_hills","tum_hi_ho","yeh_fitoor_mera"};
    public static String[] allSongs;

    public static boolean isMediaPlayerInitialized = false;

    public MediaPlayerActivity(){

    }

    private static MediaPlayerActivity instance;

    private static WeakReference<MediaPlayerActivity> instanceRef;

    public static MediaPlayerActivity getInstance() {
        return instanceRef != null ? instanceRef.get() : null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (instanceRef != null && instanceRef.get() == this) {
            instanceRef.clear();
        }
    }



    private Runnable UpdateSongTime = new Runnable() {
        @Override
        public void run() {
            s_Time = mediaPlayer.getCurrentPosition();
            startTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(s_Time), TimeUnit.MILLISECONDS.toSeconds(s_Time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(s_Time))));
            progress.setProgress(s_Time);
            handle.postDelayed(this, 100);
        }
    };

//    public void assignSong(int number){
//        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//            mediaPlayer.stop();
//        }
//        try {
//            assetFileDescriptor = getAssets().openFd(("songs/"+allSongs[number]+".mp3"));
//
//            mediaPlayer.reset();
//            mediaPlayer.setDataSource(assetFileDescriptor.getFileDescriptor(), assetFileDescriptor.getStartOffset(), assetFileDescriptor.getLength());
//            assetFileDescriptor.close();
//            mediaPlayer.prepare();
//            songname.setText(allSongs[number].replace("_"," "));
//            int images = getResources().getIdentifier(allSongs[number], "drawable", getPackageName());
//            musicImage.setImageResource(images);
//
//            dbHandler.userSongData(strUsername,allSongs[number],count = count+1);
//            dbHandler.checkUserSongData();
//
//            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener(){
//                @Override
//                public void onCompletion(MediaPlayer mediaPlayer){
//                    playNext();
//                }
//            });
//
//            findViewById(R.id.fullPlayer).setVisibility(View.VISIBLE);
//
////          mediaPlayer.start();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void assignSong(int number) {
        if (number < 0 || number >= allSongs.length) {
            return; // Prevent ArrayIndexOutOfBoundsException
        }

        // Stop any currently playing media
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();
        }

        currentSong = number;

        try {
            assetFileDescriptor = getAssets().openFd("songs/" + allSongs[number] + ".mp3");

            mediaPlayer.setDataSource(
                    assetFileDescriptor.getFileDescriptor(),
                    assetFileDescriptor.getStartOffset(),
                    assetFileDescriptor.getLength()
            );
            assetFileDescriptor.close();
            mediaPlayer.prepare();


//            Intent serviceIntent = new Intent(this, NotificationService.class);
//            ContextCompat.startForegroundService(this, serviceIntent);
//
//            NotificationService notificationService = new NotificationService();
//            notificationService.showNotification(this,
//                    allSongs[number].replace("_", " "),
//                    "Now Playing",
//                    true
//            );


            // Update UI
            songname.setText(allSongs[number].replace("_", " "));
            int imageRes = getResources().getIdentifier(allSongs[number], "drawable", getPackageName());
            if (imageRes != 0) {
//                backgroundImage.setImageResource(imageRes);
                musicImage.setImageResource(imageRes);
            } else {
                musicImage.setImageResource(R.drawable.itunes_logo_png_transparent); // fallback
//                backgroundImage.setImageResource(R.drawable.itunes_logo_png_transparent); // fallback
            }

            dbHandler.userSongData(strUsername, allSongs[number], ++count);
            dbHandler.checkUserSongData();

            mediaPlayer.setOnCompletionListener(mp -> playNext());

            // Ensure full player is visible
            findViewById(R.id.fullPlayer).setVisibility(View.VISIBLE);

            isMediaPlayerInitialized = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_player);

        instanceRef = new WeakReference<>(this);

        sharedPreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        strUsername = sharedPreferences.getString(USERNAME,null);
        System.out.println(strUsername);

        dbHandler = new DBHandler(MediaPlayerActivity.this);

        CSVHandler csvReader = new CSVHandler(getApplicationContext());
        allSongs = csvReader.getColumnData(1);

        Intent intent = getIntent();
        click_song_name =  intent.getStringExtra("songName");
        currentSong = Arrays.asList(allSongs).indexOf(click_song_name);
        if (currentSong == -1) {
            Log.w("MediaPlayer", "Song not found: " + click_song_name + " — defaulting to last song");
            currentSong = allSongs.length - 1; // fallback to last song in the list
        }
//        currentSong = intent.getIntExtra("songIndex", 0);

        musicImage = findViewById(R.id.songcover);
//        backgroundImage = findViewById(R.id.bgCover);
        previous = findViewById(R.id.prev);
        next = findViewById(R.id.next);
        playPause = findViewById(R.id.playPause);

        songname = findViewById(R.id.songname);
        startTime = findViewById(R.id.time);
        totaltime = findViewById(R.id.totaltime);
        progress = findViewById(R.id.seekbar);
        progress.setClickable(true);

        thread.start();

        Log.e("Current Song", click_song_name);

        assignSong(currentSong);

        mediaPlayer.start();

//        if (mediaPlayer == null || !isMediaPlayerInitialized) {
//            assignSong(currentSong);
//            mediaPlayer.start();
//            isMediaPlayerInitialized = true;
//        } else {
//            // MediaPlayer is already playing/resumed, just update the UI
//            updateUIWithCurrentSong();
//        }

        playPause.setBackgroundResource(R.drawable.ic_baseline_pause_24);
        getTime();

        playPause.setOnClickListener(v -> {
//            Intent serviceIntent = new Intent(this, NotificationService.class);
//            ContextCompat.startForegroundService(this, serviceIntent);
//
//            NotificationService notificationService = new NotificationService();
//            notificationService.showNotification(
//                    allSongs[currentSong].replace("_", " "),
//                    "Now Playing",
//                    true
//            );

            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
            }
            else if(!mediaPlayer.isPlaying()){
                mediaPlayer.start();
                playPause.setImageResource(R.drawable.ic_baseline_pause_24);
            }

            getTime();
            playPause.setEnabled(true);
        });

        next.setOnClickListener(v -> {
            playNext();
        });

        progress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {}

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateProgress= new Thread(){
            public  void  run(){
                int currentpos = 0;
                try {
                    while (currentpos < mediaPlayer.getDuration())
                        currentpos = mediaPlayer.getDuration();
                    progress.setProgress(currentpos);
                    sleep(800);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateProgress.start();

        previous.setOnClickListener(v -> {
            if (currentSong > 0) {
                currentSong = (currentSong - 1);
                assignSong(currentSong);
                mediaPlayer.start();
            } else {
                currentSong = allSongs.length - 1;
                assignSong(currentSong);
                mediaPlayer.start();
                Log.w("MediaPlayer", "Song not found: " + click_song_name + " — defaulting to last song");
            }
            if (!playPause.isEnabled()) {
                playPause.setEnabled(true);
            }
        });

        arrowDown = findViewById(R.id.arrowDown);
        arrowDown.setOnClickListener(v -> {
            finish(); // Close full player and go back to MainActivity
        });

    }

    private void updateUIWithCurrentSong() {
        songname.setText(allSongs[currentSong]);

        int imageRes = getResources().getIdentifier(allSongs[currentSong], "drawable", getPackageName());
        if (imageRes != 0) {
            musicImage.setImageResource(imageRes);
//            backgroundImage.setImageResource(imageRes);
        } else {
            musicImage.setImageResource(R.drawable.itunes_logo_png_transparent);
//            backgroundImage.setImageResource(R.drawable.itunes_logo_png_transparent);
        }

        if (mediaPlayer.isPlaying()) {
            playPause.setImageResource(R.drawable.ic_baseline_pause_24);
        } else {
            playPause.setImageResource(R.drawable.ic_baseline_play_arrow_24);
        }

        getTime();
    }


    public void playNext(){
        if (currentSong >= allSongs.length) {
            currentSong = 0; // Loop back to first song
        }
        if (true) {
            currentSong = Arrays.asList(allSongs).indexOf(nextSongs[num]);
            assignSong(currentSong);
            mediaPlayer.start();
            num++;
        }
        if (!playPause.isEnabled()) {
            playPause.setEnabled(true);
        }
       getTime();
    }

    private void getTime(){
        o_Time = 0;
        e_Time = mediaPlayer.getDuration();
        s_Time = mediaPlayer.getCurrentPosition();
        if (o_Time == 0) {
            progress.setMax(e_Time);
            o_Time = 1;
        }
        totaltime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(e_Time), TimeUnit.MILLISECONDS.toSeconds(e_Time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(e_Time))));
        startTime.setText(String.format("%d:%d", TimeUnit.MILLISECONDS.toMinutes(s_Time), TimeUnit.MILLISECONDS.toSeconds(s_Time) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(s_Time))));
        progress.setProgress(s_Time);
        handle.postDelayed(UpdateSongTime, 100);
    }

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            getRecommendations();
        }
    });

    public static void getRecommendations() {
        try {
            System.out.println(click_song_name);
            String urlString= "http://192.168.0.104:5000/get_recommendations?song=sukh_kalale&recs=50";

//            URL url = new URL("http://httpbin.org/get?song=sukh_kalale&recs=10");

            String apiUrl = "http://192.168.90.176:5000/get_recommendations?song=songname&recs=50";
//            String currentSongName = MediaPlayerActivity.allSongs[currentSong].replace("_", " ");
            System.out.println("allSongs[currentSong]"+currentSong);
            String formattedUrl = apiUrl.replace("songname",click_song_name);
            System.out.println(formattedUrl);
            URL url = new URL(formattedUrl);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//            System.out.println(connection.getResponseCode());

            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
//            connection.setReadTimeout(10000);
//            connection.setConnectTimeout(10000);

//            System.out.println("b");
            connection.setRequestProperty("Accept", "application/json");
//            System.out.println("c");

            System.out.println("connection.getResponseCode()");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

//            System.out.println("d");

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
            connection.getInputStream().close();
            connection.disconnect();

            JSONObject json = new JSONObject(response.toString());
            if(json.has("recomms")) {
                JSONArray recomms = json.getJSONArray("recomms");
                for (int i = 0; i < recomms.length(); i++) {
                    recommendations.add(recomms.getString(i));
//                    System.out.println(recomms.getString(i));
//                    System.out.println("recommendations: " + recommendations);
                }
                System.out.println("Recommendations: " + recommendations);
                nextSongs = recommendations.toArray(new String[0]);
                num = 0;
                System.out.println("nextSongs: "+ Arrays.toString(nextSongs));
                recommendations.clear();
            }
            else {
                System.out.println("Invalid request");
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

