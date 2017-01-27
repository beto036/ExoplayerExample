package com.example.admin.exoplayerexample;

import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveVideoTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

//    private static final String VIDEO_URI =
//            "http://www-itec.uni-klu.ac.at/ftp/datasets/DASHDataset2014/BigBuckBunny/4sec/BigBuckBunny_4s_onDemand_2014_05_09.mpd";

    private static final String VIDEO_URI =
            "http://techslides.com/demos/sample-videos/small.mp4";

    private SimpleExoPlayer player;
    private SimpleExoPlayerView simpleExoPlayerView;
    private Handler mainHandler;
    private TrackSelection.Factory videoTrackSelectionFactory;
    private TrackSelector trackSelector;
    private LoadControl loadControl;
    private DataSource.Factory dataSourceFactory;
    private MediaSource videoSource;
    private Uri uri;
    private String userAgent;
    private static final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleExoPlayerView = (SimpleExoPlayerView)findViewById(R.id.mainSimpleExoPlayer);
        userAgent = Util.getUserAgent(this,"SimpleDashExoPlayer");
        createPlayer();
        attachPlayerView();
        preparePlayer();
    }

    // Create TrackSelection Factory, Track Selector, Handler, Load Control, and ExoPlayer Instance
    public void createPlayer(){
        mainHandler = new Handler();
        videoTrackSelectionFactory = new AdaptiveVideoTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(this,trackSelector,loadControl);
    }

    // Set player to SimpleExoPlayerView
    public void attachPlayerView(){
        simpleExoPlayerView.setPlayer(player);
    }

    // Build Data Source Factory, Dash Media Source, and Prepare player using videoSource
    public void preparePlayer(){
        uriParse();
        dataSourceFactory = buildDataSourceFactory(bandwidthMeter);
//        videoSource = new DashMediaSource(uri,buildDataSourceFactory(null),new DefaultDashChunkSource.Factory(dataSourceFactory),mainHandler,null);
        videoSource = new ExtractorMediaSource(uri, dataSourceFactory, new DefaultExtractorsFactory(),mainHandler,null);
        player.prepare(videoSource);
    }

    // Parse VIDEO_URI and Save at uri variable
    public void uriParse(){
        uri = Uri.parse(VIDEO_URI);
    }

    // Build Data Source Factory using DefaultBandwidthMeter and HttpDataSource.Factory
    private DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter){
        return new DefaultDataSourceFactory(this, bandwidthMeter, buildHttpDataSourceFactory(bandwidthMeter));
    }

    // Build Http Data Source Factory using DefaultBandwidthMeter
    private HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter){
        return new DefaultHttpDataSourceFactory(userAgent, bandwidthMeter);
    }

    // Activity onStop, player must be release because of memory saving
    @Override
    public void onStop(){
        super.onStop();
        player.release();
    }
}
