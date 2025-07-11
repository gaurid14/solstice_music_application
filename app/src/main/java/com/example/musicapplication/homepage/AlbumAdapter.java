package com.example.musicapplication.homepage;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.musicapplication.R;

import java.util.List;

public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder> {

    private List<Album> albumList;
    private SectionAdapter.OnAlbumClickListener listener;

    public AlbumAdapter(List<Album> albumList, SectionAdapter.OnAlbumClickListener listener) {
        this.albumList = albumList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AlbumViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_album, parent, false);
        return new AlbumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlbumViewHolder holder, int position) {
        Album album = albumList.get(position);
        holder.albumImage.setImageResource(album.getImageResId());
        holder.albumImage.setOnClickListener(v -> {
            if (listener != null) {
                listener.onAlbumClick(album.getSongName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return albumList.size();
    }

    static class AlbumViewHolder extends RecyclerView.ViewHolder {
        ImageView albumImage;

        AlbumViewHolder(View itemView) {
            super(itemView);
            albumImage = itemView.findViewById(R.id.albumImage);
        }
    }
}

