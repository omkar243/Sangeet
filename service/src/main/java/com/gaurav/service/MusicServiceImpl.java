package com.gaurav.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.gaurav.domain.interfaces.MusicInteractor;
import com.gaurav.domain.interfaces.MusicService;

import java.io.IOException;

import io.reactivex.Completable;

/*
 * Todos for tomorrow:
 *
 * [DONE] Make this service actually a service and let it start with appropriate place and handle itself well
 * [DONE] make communication to this service clear. - bind in application init
 * [DONE] Handle illegal states in media player
 *
 * [DONE] Clean splash activity animation and activity code itself
 * [DONE] clean musicpplication class
 * [DONE] clean home activity
 * [DONE] clean viewmodel and rvadapter
 * [DONE] clean fakefragment
 *
 * [] To give events to viewmodel instead of commands and actions flowing out from view models
 * [] better state reducer code
 * [] clean music interactor impl
 *
 * [] remove the useless data model transformations
 *
 * [] show albums and artists and playlists
 * [] handle their clicking and stuff
 * [] make presentation show MusicState data - observe music state
 * [] implement music state save
 * [] add callback listeners for updating duration
 *
 *
 * [] add other functionality like play pause next  - view queue
 * [] implement addToQueue for people
 * [] implement playNext
 *
 * [] Presentation layer + notification
 * */
public class MusicServiceImpl extends Service implements MusicService {

    MusicInteractor musicInteractor;
    MediaPlayer mediaPlayer;

    MusicServiceBinder binder;

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer = new MediaPlayer();
        startForeground(101, getNotification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("MusicServiceImpl", "Binding ... ");
        if (binder == null) {
            binder = new MusicServiceBinder();
        }
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d("MusicServiceImpl", "Unbinding ... ");
        binder = null;
        return false;
    }

    @Override
    public void attachMusicInteractor(MusicInteractor musicInteractor) {
        this.musicInteractor = musicInteractor;
    }

    @Override
    public Completable play(String path) {
        return Completable.create(emitter -> {
            mediaPlayer.reset();
            try {
                mediaPlayer.setDataSource(path);
                mediaPlayer.prepare();
                mediaPlayer.start();
                emitter.onComplete();
            } catch (IOException e) {
                emitter.onError(e);
            }
        });

    }

    private Notification getNotification() {
        return new Notification.Builder(this)
                .setSmallIcon(R.drawable.notif_icon)
                .setContentTitle("Sangeet app")
                .setContentText("Sangeet is playing").build();

    }

    public class MusicServiceBinder extends Binder {
        public MusicService getService() {
            return MusicServiceImpl.this;
        }
    }
}