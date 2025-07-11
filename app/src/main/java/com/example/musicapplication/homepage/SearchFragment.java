package com.example.musicapplication.homepage;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.musicapplication.MediaPlayerActivity;
import com.example.musicapplication.R;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    public SearchFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        AutoCompleteTextView autoCompleteTextView = view.findViewById(R.id.searchBar);
        ListView recentSearchList = view.findViewById(R.id.recentSearchList);

        List<String> songList = new ArrayList<>();

        // Read multiple CSV files
        String[] csvFiles = {"Files/english.csv", "Files/hindi.csv", "Files/marathi.csv", "Files/tamil.csv", "Files/punjabi.csv"};
        for (String fileName : csvFiles) {
            try (InputStream inputStream = getContext().getAssets().open(fileName);
                 CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
                List<String[]> rows = reader.readAll();
                for (String[] row : rows) {
                    if (row.length > 0) songList.add(row[0]);
                }
            } catch (IOException | CsvException e) {
                e.printStackTrace();
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),  R.layout.dropdown_item_white, R.id.dropItem, songList);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedSong = (String) parent.getItemAtPosition(position);

            String formattedSong = selectedSong.toLowerCase().replace(" ", "_");
            Log.e("Selected song name", formattedSong);

            Intent intent = new Intent(getActivity(), MediaPlayerActivity.class);
            intent.putExtra("songName", formattedSong);  // Send the song name to MediaPlayerActivity
            startActivity(intent);
        });


        return view;
    }
}
