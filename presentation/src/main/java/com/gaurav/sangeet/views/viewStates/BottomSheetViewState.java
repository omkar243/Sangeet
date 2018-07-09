package com.gaurav.sangeet.views.viewStates;

import com.gaurav.domain.MusicState;
import com.gaurav.domain.models.Song;

public class BottomSheetViewState {
    private MusicState musicState;
    private boolean updateCurrentSongDetails;
    private Song currentSong;

    public BottomSheetViewState(MusicState musicState, boolean updateCurrentSongDetails,
                                Song currentSong) {
        this.musicState = musicState;
        this.updateCurrentSongDetails = updateCurrentSongDetails;
        this.currentSong = currentSong;
    }

    public MusicState getMusicState() {
        return musicState;
    }

    public boolean isUpdateCurrentSongDetails() {
        return updateCurrentSongDetails;
    }

    public Song getCurrentSong() {
        return currentSong;
    }
}
