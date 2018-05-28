package com.gaurav.domain.interfaces;

import com.gaurav.domain.models.Album;
import com.gaurav.domain.models.Artist;
import com.gaurav.domain.models.Song;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;

public interface MusicRepository {

    Completable init();

    Single<List<Song>> getAllSongs();
    Single<List<Album>> getAllAlbums();
    Single<List<Artist>> getAllArtists();
}
