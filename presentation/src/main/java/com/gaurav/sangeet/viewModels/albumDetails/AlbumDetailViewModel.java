package com.gaurav.sangeet.viewModels.albumDetails;

import android.arch.lifecycle.MutableLiveData;

import com.gaurav.domain.models.Song;
import com.gaurav.domain.usecases.actions.PlayAlbumAction;
import com.gaurav.domain.usecases.interfaces.CommandUseCases;
import com.gaurav.domain.usecases.interfaces.FetchUseCases;
import com.gaurav.sangeet.MusicApplication;
import com.gaurav.sangeet.di.Injector;
import com.gaurav.sangeet.viewModels.BaseViewModel;
import com.gaurav.sangeet.views.interfaces.AlbumDetailView;
import com.gaurav.sangeet.views.uiEvents.albumDetails.PlayAlbumDetailUIEvent;
import com.gaurav.sangeet.views.viewStates.AlbumDetailViewState;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AlbumDetailViewModel extends BaseViewModel {
    @Inject
    FetchUseCases fetchUseCases;
    @Inject
    CommandUseCases commandUseCases;
    private AlbumDetailView albumDetailView;
    private MutableLiveData<AlbumDetailViewState> state;

    public AlbumDetailViewModel( AlbumDetailView albumDetailView, long albumId) {
        Injector.get().inject(this);
        this.albumDetailView = albumDetailView;
        bindIntents();

        state = new MutableLiveData<>();
        compositeDisposable.add(
                fetchUseCases.getAlbum(albumId)
                        .observeOn(AndroidSchedulers.mainThread())
                        .doOnSubscribe(__ -> state.setValue(new AlbumDetailViewState.Loading()))
                        .subscribe(album -> state.setValue(new AlbumDetailViewState.Result(album)),
                                throwable -> state.setValue(new AlbumDetailViewState.Error())));
    }

    @Override
    public void bindIntents() {
        compositeDisposable.add(albumDetailView.getUIEvents()
                .map(albumDetailUIEvent -> {
                    if (albumDetailUIEvent instanceof PlayAlbumDetailUIEvent) {
                        Song song = ((PlayAlbumDetailUIEvent) albumDetailUIEvent).getSong();
                        if (song == null) {
                            song = ((PlayAlbumDetailUIEvent) albumDetailUIEvent).getAlbum().songSet.first();
                        }
                        return new PlayAlbumAction(((PlayAlbumDetailUIEvent) albumDetailUIEvent).getAlbum(), song);
                    }
                    return new PlayAlbumAction(null, null);
                }).subscribe(playAlbumAction -> commandUseCases.actionSubject()
                        .onNext(playAlbumAction)));
    }

    public MutableLiveData<AlbumDetailViewState> getState() {
        return state;
    }
}
