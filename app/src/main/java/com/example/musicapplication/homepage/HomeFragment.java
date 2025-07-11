package com.example.musicapplication.homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.MediaPlayerActivity;
import com.example.musicapplication.R;
import com.example.musicapplication.SignupScreen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    public static final String SHARED_PREFS = "shared_prefs";
    public static String songPath;

    private SharedPreferences sharedPreferences;
    private Button logoutButton;
    private RecyclerView sectionRecyclerView;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        // Shared Preferences
        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);

        // RecyclerView
        sectionRecyclerView = view.findViewById(R.id.sectionRecyclerView);
        sectionRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        sectionRecyclerView.setAdapter(new SectionAdapter(getSections(), this::setMediaPlayer));

        // Logout Button
//        logoutButton = view.findViewById(R.id.logout);
//        logoutButton.setOnClickListener(v -> logout());

        return view;
    }

    private List<Section> getSections() {
        List<Section> sections = new ArrayList<>();

        List<Album> albumList = Arrays.asList(
                new Album(R.drawable.ajab_si, "ajab_si"),
                new Album(R.drawable.creepin, "creepin"),
                new Album(R.drawable.reminder, "reminder"),
                new Album(R.drawable.kya_mujhe_pyar_hai, "kya_mujhe_pyar_hai"),
                new Album(R.drawable.halamaithi_habibo, "halamaithi_habibo"),
                new Album(R.drawable.jaan_nisaar, "jaan_nisaar")
        );

        for (int i = 0; i < 15; i++) {
            sections.add(new Section("Section " + (i + 1), albumList));
        }

        return sections;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(requireActivity(), SignupScreen.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private void setMediaPlayer(String songName) {
        songPath = "songs/" + songName + ".mp3";
        Intent intent = new Intent(requireContext(), MediaPlayerActivity.class);
        intent.putExtra("songName", songName);
        startActivity(intent);
    }
}
