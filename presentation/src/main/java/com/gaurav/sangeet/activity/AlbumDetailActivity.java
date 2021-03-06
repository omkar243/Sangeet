package com.gaurav.sangeet.activity;

import android.arch.lifecycle.ViewModelProviders;
import android.content.res.ColorStateList;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;

import com.gaurav.domain.models.Album;
import com.gaurav.sangeet.R;
import com.gaurav.sangeet.utils.ItemClickSupport;
import com.gaurav.sangeet.viewmodels.albumdetails.AlbumDetailViewModel;
import com.gaurav.sangeet.viewmodels.albumdetails.AlbumDetailViewModelFactory;
import com.gaurav.sangeet.viewmodels.bottomsheet.BottomSheetViewModel;
import com.gaurav.sangeet.views.helperviews.DialogViewHelper;
import com.gaurav.sangeet.views.implementations.albumdetails.AlbumDetailsSongsRVAdapter;
import com.gaurav.sangeet.views.implementations.bottomsheet.BottomSheetViewImpl;
import com.gaurav.sangeet.views.interfaces.AlbumDetailView;
import com.gaurav.sangeet.views.uievents.albumdetails.AlbumDetailUIEvent;
import com.gaurav.sangeet.views.uievents.albumdetails.PlayAlbumDetailUIEvent;
import com.gaurav.sangeet.views.viewstates.AlbumDetailViewState;

import java.util.ArrayList;

import io.reactivex.subjects.PublishSubject;

public class AlbumDetailActivity extends AppCompatActivity implements AlbumDetailView {

    private AlbumDetailViewModel viewModel;
    private PublishSubject<AlbumDetailUIEvent> uiEventsSubject;

    // Views
    private AppBarLayout appBarLayout;
    private Toolbar toolbar;
    private TextView artistTotalSongs;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView albumArtwork;
    private FloatingActionButton playAlbumButton;
    private RecyclerView albumSongRecyclerView;
    private BottomSheetViewImpl bottomSheetViewImpl;

    // View related objects
    private BottomSheetBehavior bottomSheetBehavior;
    private AlbumDetailsSongsRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        setContentView(R.layout.activity_album_detail);

        uiEventsSubject = PublishSubject.create();
        initViews();
        setupViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        viewModel = ViewModelProviders.of(this,
                new AlbumDetailViewModelFactory(getIntent().getLongExtra("albumId",
                        -1))).get(AlbumDetailViewModel.class);
        viewModel.attachAlbumDetailView(this);
        viewModel.getState().observe(this, this::render);
    }

    @Override
    public void onEnterAnimationComplete() {
        playAlbumButton.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(getResources().getInteger(R.integer.detailPlayButtonAnimDuration))
                .start();
        bottomSheetViewImpl.getBaseView().setAlpha(1f);
        bottomSheetViewImpl.getBaseView().startAnimation(
                AnimationUtils.loadAnimation(this, R.anim.bottom_sheet_slide_up));
    }

    @Override
    public void render(AlbumDetailViewState state) {
        if (state instanceof AlbumDetailViewState.Loading) {
            // show loading
        } else if (state instanceof AlbumDetailViewState.Error) {
            // show error
        } else {
            Album album = ((AlbumDetailViewState.Result) state).getAlbum();
            collapsingToolbarLayout.setTitle(album.name);
            artistTotalSongs.setText(String.format("%s • %s %s",
                    album.multipleArtists ? "Various Artists" : album.artistName,
                    album.songSet.size(), album.songSet.size() == 1 ? "Song" : "Songs"));
            adapter.updateData(new ArrayList<>(album.songSet));
            LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(this,
                    R.anim.song_list_layout_animation);
            controller.getAnimation().setStartOffset(getResources().getInteger(R.integer.detailListOffset));
            albumSongRecyclerView.setLayoutAnimation(controller);
            albumSongRecyclerView.scheduleLayoutAnimation();

            String artworkPath = album.songSet.first().artworkPath;
            if (!artworkPath.equals("null")) {
                albumArtwork.setImageBitmap(BitmapFactory.decodeFile(artworkPath));
                new Palette.Builder(BitmapFactory.decodeFile(artworkPath))
                        .generate(this::updateColors);
            } else {
                albumArtwork.setImageDrawable(getDrawable(R.drawable.default_item_icon));
                new Palette.Builder(BitmapFactory.decodeResource(getResources(),
                        R.drawable.default_item_icon)).generate(this::updateColors);
            }
        }
    }

    @Override
    public PublishSubject<AlbumDetailUIEvent> getUIEvents() {
        return uiEventsSubject;
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED ||
                bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_DRAGGING ||
                bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_SETTLING) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            finishWithAnimation();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finishWithAnimation();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
        appBarLayout = findViewById(R.id.appBarLayout);
        toolbar = findViewById(R.id.toolbar);
        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar);
        artistTotalSongs = findViewById(R.id.artistTotalSongs);
        albumArtwork = findViewById(R.id.albumArtwork);
        playAlbumButton = findViewById(R.id.playAlbumButton);
        albumSongRecyclerView = findViewById(R.id.recyclerView);
        bottomSheetViewImpl = new BottomSheetViewImpl(findViewById(R.id.bottom_sheet));
    }

    private void setupViews() {
        // create view related objects
        adapter = new AlbumDetailsSongsRVAdapter(new ArrayList<>());
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetViewImpl.getBaseView());

        // setup toolbar
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> {
            artistTotalSongs.setTranslationY(verticalOffset / 3);
        });
        toolbar.setTitleTextAppearance(this, R.style.toolbarTitleFont);
        toolbar.setTitleTextColor(getColor(R.color.toolbarTitleColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);

        // setup album play button
        playAlbumButton.setOnClickListener(v -> uiEventsSubject.onNext(new PlayAlbumDetailUIEvent(
                ((AlbumDetailViewState.Result) viewModel.getState().getValue()).getAlbum(),
                null)));
        playAlbumButton.setScaleX(0f);
        playAlbumButton.setScaleY(0f);

        // setup song recycler view
        albumSongRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        albumSongRecyclerView.setHasFixedSize(true);
        albumSongRecyclerView.setAdapter(adapter);
        ItemClickSupport.addTo(albumSongRecyclerView).setOnItemClickListener(
                (albumSongRecyclerView, position, v) -> uiEventsSubject.onNext(
                        new PlayAlbumDetailUIEvent(((AlbumDetailViewState.Result)
                                viewModel.getState().getValue()).getAlbum(),
                                adapter.getSong(position))));
        ItemClickSupport.addTo(albumSongRecyclerView)
                .setOnItemLongClickListener((recyclerView, position, v) -> {
                    new DialogViewHelper(this, adapter.getSong(position),
                            false, true).getDialog().show();
                    return true;
                });

        // TODO: 7/15/18 FInd a better way tro manage bottom sheet and it's info
        bottomSheetBehavior.setHideable(false);
        bottomSheetViewImpl.getBaseView().setAlpha(0f);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    // TODO: 7/8/18 add menu items for actions like showQueue, gotoAlbum,gotoArtist
                    // add to playlist here. Also hide these menus when state becomes moving or
                    // collapsed.
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                float scale = 1 - 3 * slideOffset;
                scale = scale < 0 ? 0 : scale;
                playAlbumButton.setScaleX(scale);
                playAlbumButton.setScaleY(scale);
                // TODO: 7/8/18 UI task: change the position of views according to this.
            }
        });
        BottomSheetViewModel viewModel = ViewModelProviders.of(this).get(BottomSheetViewModel.class);
        viewModel.attachBottomSheetView(bottomSheetViewImpl);
        viewModel.setOnClickListener(v -> {
            if (bottomSheetBehavior.getState() ==
                    BottomSheetBehavior.STATE_COLLAPSED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        viewModel.getViewState().observe(this, bottomSheetViewImpl::render);
    }

    private void updateColors(Palette palette) {
        Palette.Swatch swatch = palette.getVibrantSwatch();
        if (swatch == null) swatch = palette.getDominantSwatch();
        collapsingToolbarLayout.setContentScrimColor(swatch.getRgb());
        collapsingToolbarLayout.setStatusBarScrimColor(swatch.getRgb());
        collapsingToolbarLayout.setExpandedTitleColor(swatch.getTitleTextColor());
        collapsingToolbarLayout.setCollapsedTitleTextColor(swatch.getTitleTextColor());
        artistTotalSongs.setTextColor(swatch.getBodyTextColor());
        playAlbumButton.setBackgroundTintList(ColorStateList.valueOf(
                palette.getVibrantColor(getColor(R.color.colorAccent))));
    }

    private void finishWithAnimation() {
        finish();
        overridePendingTransition(R.anim.scale_down, R.anim.detail_fade_out);
    }
}
