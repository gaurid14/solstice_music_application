package com.example.musicapplication.homepage;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.musicapplication.R;
import com.example.musicapplication.SignupScreen;

public class SettingsFragment extends Fragment {

    public static final String SHARED_PREFS = "shared_prefs";
    private SharedPreferences sharedPreferences;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        // Load username from SharedPreferences
//        SharedPreferences prefs = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        sharedPreferences = requireContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        String name = sharedPreferences.getString("username", "User");

        TextView nameText = view.findViewById(R.id.name);
        nameText.setText(name);

        TextView logoutText = view.findViewById(R.id.logout_text);
        logoutText.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
            logout();

            // Example logout logic (optional): clear SharedPreferences
            // SharedPreferences.Editor editor = prefs.edit();
            // editor.clear();
            // editor.apply();
        });

        return view;
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
}
