package com.gaurav.domain.musicState;

import com.gaurav.domain.models.Song;

import java.util.ArrayList;
import java.util.List;

public class MusicStateBuilder {
    private boolean showStatus = false;
    private long progress = 0;
    private int currentSongIndex = 0;
    private Song currentSong = null;
    private boolean shuffle = true;
    private boolean repeat = true;
    private List<Song> songQueue = new ArrayList<>();
    private List<Song> originalSongQueue = new ArrayList<>();
    private boolean disablePrev = false;
    private boolean isPlaying = false;

    public MusicStateBuilder() {
    }

    public MusicStateBuilder(boolean showStatus, long progress, int currentSongIndex,
                             Song currentSong, boolean shuffle, boolean repeat,
                             List<Song> songQueue, List<Song> originalSongQueue,
                             boolean disablePrev, boolean isPlaying) {
        this.showStatus = showStatus;
        this.progress = progress;
        this.currentSongIndex = currentSongIndex;
        this.currentSong = currentSong;
        this.shuffle = shuffle;
        this.repeat = repeat;
        this.songQueue = songQueue;
        this.originalSongQueue = originalSongQueue;
        this.disablePrev = disablePrev;
        this.isPlaying = isPlaying;
    }

    public MusicStateBuilder setShowStatus(boolean showStatus) {
        this.showStatus = showStatus;
        return this;
    }

    public MusicStateBuilder setProgress(long progress) {
        this.progress = progress;
        return this;
    }

    public MusicStateBuilder setCurrentSongIndex(int currentSongIndex) {
        this.currentSongIndex = currentSongIndex;
        this.currentSong = currentSongIndex < songQueue.size() ?
                songQueue.get(currentSongIndex) : null;
        return this;
    }

    public MusicStateBuilder setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
        return this;
    }

    public MusicStateBuilder setRepeat(boolean repeat) {
        this.repeat = repeat;
        return this;
    }

    public MusicStateBuilder setSongQueue(List<Song> songQueue) {
        this.songQueue = songQueue;
        return this;
    }

    public MusicStateBuilder setDisablePrev(boolean disablePrev) {
        this.disablePrev = disablePrev;
        return this;
    }

    public MusicStateBuilder setPlaying(boolean playing) {
        isPlaying = playing;
        return this;
    }

    public MusicStateBuilder setOriginalSongQueue(List<Song> originalSongQueue) {
        this.originalSongQueue = originalSongQueue;
        return this;
    }

    public MusicState build() {
        return new MusicState(showStatus, progress, currentSongIndex, currentSong, shuffle,
                repeat, songQueue, originalSongQueue, disablePrev, isPlaying);
    }
}