package com.gaurav.sangeet.views.implementations.songs;

import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaurav.domain.models.Song;
import com.gaurav.sangeet.R;
import com.gaurav.sangeet.utils.Utils;

import java.util.List;

public class SongsRVAdapter extends RecyclerView.Adapter<SongsRVAdapter.SongItemViewHolder> {

    private List<Song> data;
    private Drawable defaultIcon;

    // helper private variables
    private Song song;

    public SongsRVAdapter(List<Song> data, Drawable defaultIcon) {
        this.data = data;
        this.defaultIcon = defaultIcon;
    }

    @NonNull
    @Override
    public SongItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SongItemViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.song_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SongItemViewHolder holder, int position) {
        song = data.get(position);
        if (!song.artworkPath.equals("null")) {
            holder.songIcon.setImageBitmap(BitmapFactory.decodeFile(song.artworkPath));
        } else {
            // todo set a default icon here
            holder.songIcon.setImageDrawable(defaultIcon);
        }
        holder.songTitle.setSelected(true);
        holder.songArtistAlbum.setSelected(true);
        holder.songTitle.setText(song.title);
        holder.songArtistAlbum.setText(String.format("%s • %s", song.artist, song.album));
        holder.songDuration.setText(Utils.convertLongToDuration(song.duration));
    }

    @Override
    public void onViewRecycled(@NonNull SongItemViewHolder holder) {
        holder.songTitle.setSelected(false);
        holder.songArtistAlbum.setSelected(false);
        holder.songIcon.setImageDrawable(null);
        super.onViewRecycled(holder);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).songId;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public void updateData(List<Song> newData) {
        this.data = newData;
        notifyDataSetChanged();
    }

    public Song getSong(int pos) {
        return data.get(pos);
    }

    class SongItemViewHolder extends RecyclerView.ViewHolder {
        ImageView songIcon;
        TextView songTitle;
        TextView songArtistAlbum;
        TextView songDuration;

        SongItemViewHolder(View itemView) {
            super(itemView);
            songIcon = itemView.findViewById(R.id.songIcon);
            songTitle = itemView.findViewById(R.id.songTitle);
            songArtistAlbum = itemView.findViewById(R.id.songArtistAlbum);
            songDuration = itemView.findViewById(R.id.songDuration);
        }
    }
}
