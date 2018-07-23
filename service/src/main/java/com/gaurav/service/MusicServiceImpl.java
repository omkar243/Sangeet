package com.gaurav.service;import android.app.Service;import android.content.Intent;import android.media.MediaPlayer;import android.os.Binder;import android.os.IBinder;import android.util.Log;import com.gaurav.domain.interfaces.MusicService;import java.io.IOException;import java.util.concurrent.TimeUnit;import io.reactivex.Observable;import io.reactivex.disposables.Disposable;import io.reactivex.subjects.PublishSubject;/* * Total: * ====================================================================== * Application start - make database,repo,statemanager etc. Init them all -> move to home activity * View implements ViewInterface. View renders ViewState. View emits UIEvents. * ViewModel listens to view's UIEvents. View listens to * live data of it's ViewState in the ViewModel. * * ViewModel asks for data to FetchUseCase and put it into it's live data of viewState * , which the View has subscribed to. * * ViewModel transforms UIEvent to Action and send it to the useCase. * UseCase executes the action. Updates the state to state manager. State manager broadcasts the * new state. ViewModel updates the ViewState according to the new MusicState broadcasted. * * * ### UI tasks * =================================================================== * Inspirations: https://www.uplabs.com/posts/daily-ui-music-player, *               https://www.uplabs.com/posts/dark-material-music-app-ui * * [Done] Think of all colors from different screens required - * Color primary dark - status bar * Color primary - toolbar * Color accent - tab layout dot = bottom sheet button color * Color primary two - bottom sheet color collapsed * Color accent two - use anywhere * [Done] 8 color schemes - I love coolors.co * (Two accents, two primary, two dark shade - two shades down of primary) * Light themes: * https://coolors.co/237eaf-243687-fdfffc-ff9f1c-ce3030 * https://coolors.co/f87060-003971-4171af-42ad51-ffc857 * https://coolors.co/009ea0-ffba49-ef5b5b-4b2e46-b4b8bb * https://coolors.co/dbb13b-478060-3b597b-0a0e29-efac73 * Dark themes: * https://coolors.co/95dce6-6564db-232ed1-101d42-0d1317 * https://coolors.co/494949-4a314d-ff5d73-1b5299-4381c1 * https://coolors.co/040f0f-248232-2ba84a-2d3a3a-fcfffc * https://coolors.co/3c1518-69140e-a44200-d58936-f2f3ae * [Done] Find logo Design apps * Gravit * Figma - design and maintain  - prototype, wireframe. * Zeplin - design and maintain - prototype, wireframe. * avocode - design to measurements * Krita - digital paint art * [] Design logo - in different colors - Soundwaves - they can animate and write some name. * [] Lottie - animate your logo. Show it now. * [] Find good design apps * * * ### Logic tasks * =================================================================== * [] Make all screens work * Songs item -> play or go to. * album -> goto * [] Design *      Overall structure - toolbar, status bar, tab layout *          Toolbar -> Sangeet title font and color, Toolbar bgcolor *          Tablayout -> bg color, selected tab text color,  unselected tab color, indicator color *                      tablyout a little margin at the bottom *      Songs list, Songs menu *          Song title font, Song title color *          Artist album font and color *          icon rounded off *          duration font and color *          icon default artwork *          divider has gaps from the edges *          menu -> long press animation leading to dialog pop up with animation *          ripple on song click *          add cool scrollbar effect with letters to recycler view *          show playing gif if song is playing -> change this subtly.Use picasso. *      Album list, album menu *          album list item in form of cards *          album title font and color *          artist and total songs font and color *          default artwork *          ripple on click according to song color *          add cool scrollbar effect with letters to recycler view *          menu -> long press animation leading to a dialog which follows same ripple principle *      Artist list, Album menu, Artist menu *          same as above *      Single album view, SIngle artist view *          animation between activity i.e. introducing a new back button and *          new intro to album/artist view + same adapters as before *      Bottom sheet view (SeekbarMovementAction) * [] Settings screen * [] Search screen and logic * [] Update colors and fonts * [] Very colourful notification * [] Logo animation * */public class MusicServiceImpl extends Service implements MusicService {    private MediaPlayer mediaPlayer;    private MusicServiceBinder binder;    private Disposable progressDisposable;    private PublishSubject<Integer> progressSubject;    private PublishSubject<Boolean> songCompleteSubject;    private boolean mediaPlayerSet = true;    @Override    public void onCreate() {        super.onCreate();        mediaPlayer = new MediaPlayer();        prepareObservablesAndEmitters();        // TODO: 7/15/18 Make a notification    }    @Override    public IBinder onBind(Intent intent) {        Log.d("MusicServiceImpl", "Binding ... ");        if (binder == null) {            binder = new MusicServiceBinder();        }        return binder;    }    @Override    public boolean onUnbind(Intent intent) {        Log.d("MusicServiceImpl", "Unbinding ... ");        binder = null;        return false;    }    @Override    public PublishSubject<Integer> observeProgress() {        return progressSubject;    }    @Override    public PublishSubject<Boolean> observeSongCompletion() {        return songCompleteSubject;    }    @Override    public void play(String path) {        mediaPlayer.reset();        if (progressDisposable != null && !progressDisposable.isDisposed()) {            progressDisposable.dispose();        }        try {            mediaPlayerSet = true;            mediaPlayer.setDataSource(path);            mediaPlayer.prepare();            mediaPlayer.start();            mediaPlayer.setOnCompletionListener(__ -> songCompleteSubject.onNext(true));            progressDisposable = Observable.interval(1, TimeUnit.SECONDS)                    .doOnNext(aLong -> {                        if (mediaPlayer.isPlaying()) {                            progressSubject.onNext(mediaPlayer.getCurrentPosition());                        }                    })                    .subscribe();        } catch (IOException e) {            e.printStackTrace();        }    }    @Override    public void pause() {        if (mediaPlayer.isPlaying()) {            mediaPlayer.pause();        }    }    @Override    public void resume() {        mediaPlayer.start();    }    @Override    public void reset() {        mediaPlayerSet = false;        mediaPlayer.reset();    }    @Override    public boolean isMediaPlayerSet() {        return mediaPlayerSet;    }    @Override    public void onDestroy() {        if (progressDisposable != null && !progressDisposable.isDisposed()) {            progressDisposable.dispose();        }        super.onDestroy();    }    /*     * Helper functions and classes     * */    private void prepareObservablesAndEmitters() {        progressSubject = PublishSubject.create();        songCompleteSubject = PublishSubject.create();    }    public class MusicServiceBinder extends Binder {        public MusicService getService() {            return MusicServiceImpl.this;        }    }}