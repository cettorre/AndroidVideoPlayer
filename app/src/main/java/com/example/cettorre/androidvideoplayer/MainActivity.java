package com.example.cettorre.androidvideoplayer;


import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements VideoRendererEventListener {


    private SimpleExoPlayerView simpleExoPlayerView;
    private SimpleExoPlayer player;
    int playerStateCount=-2;

    int paused =0;
    int restarted =0;

    public int getPausedTimes(int count){

        int result=0;

        if(count==0){
            result=0;
        }else if(count==1){
            result=0;
        }else if (count>1&&player.isPlayingAd())
            result=playerStateCount/2-1;
        else if (count>1&&!player.isPlayingAd())
            result=playerStateCount/2;

        return result;
    }



    public int getRestartedTimes(int count){
        int result=0;

        if(count==0){
            result=0;
        }
        else if (count==1){
            result=1;
        }
        else if (count>1&&player.isPlayingAd())
            result=playerStateCount/2;
        else if (count>1&&!player.isPlayingAd())
            result=playerStateCount/2-1;

        return result;


    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TrackSelector
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        //LoadControl
        LoadControl loadControl = new DefaultLoadControl();

        //Player
        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector, loadControl);
        simpleExoPlayerView = new SimpleExoPlayerView(this);
        simpleExoPlayerView = findViewById(R.id.player_view);

        //Media controller
        simpleExoPlayerView.setUseController(true);
        simpleExoPlayerView.requestFocus();

        // View
        simpleExoPlayerView.setPlayer(player);

        //Uri
        Uri m3u8VideoUri = Uri.parse("http://www.streambox.fr/playlists/x36xhzz/x36xhzz.m3u8");

        //It measures the bandwidth during playback.
        DefaultBandwidthMeter bandwidthMeterA = new DefaultBandwidthMeter();

        //Produces DataSource instances through which media data is loaded.
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this, Util.getUserAgent(this, "exoplayer2example"), bandwidthMeterA);

        //MediaSource
        MediaSource videoSource = new HlsMediaSource(m3u8VideoUri, dataSourceFactory, 1, null, null);

        final LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource);

        // Prepare the player with the source.
        player.prepare(loopingSource);


        player.addListener(new Player.EventListener() {


            @Override
            public void onLoadingChanged(boolean isLoading) {
                //when video starts to load
                //Video starts to load(first play only). -> start message

                Log.i("videop", "Listener-onLoadingChanged...isLoading:" + isLoading);


            }

            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                Log.i("videop", "Listener-onTracksChanged...");


         //       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         //           Log.i("videop", "Listener-..." + player.getDuration() + LocalDateTime.now());
         //       }


            }

            List<Long> timeList = new ArrayList<>();
            List<Long> elapsedList = new ArrayList<>();

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                Log.i("video_p", "Listener-onPlayerStateChanged...");
                playerStateCount++;

                Log.i("video_p", "Listener-onPlayerStateChanged...");
                Log.i("video_p", "content position: " + (player.getContentPosition() / 1000) + "s.");
                Log.i("video_p", "current position:  "+ player.getCurrentPosition() / 1000+ "s.");

                Log.i("video_p", "Left: " + ((player.getDuration() - player.getContentPosition()) / 1000) + "s.");
                Log.e("video_p", "Duration: " + (player.getDuration() / 1000) + "s.");
                Log.e("video_p", "count: "+playerStateCount );


                timeList.add(player.getCurrentPosition());

                long elapsed=0;
                if(timeList.size()>2)
                    elapsed=(timeList.get((timeList.size()-1))-(timeList.get(timeList.size()-2)));

                Log.e("video_p", "elapsed: " + elapsed/1000 +"s");
                elapsedList.add(elapsed);


                paused = getPausedTimes(playerStateCount);
                restarted = getRestartedTimes(playerStateCount);


                Log.e("video_p", "paused: "+ paused);
                Log.e("video_p", "restarted: "+ restarted);
                Log.e("video_p", "elapsedList: "+ elapsedList);
                Log.e("video_p", "timeList: "+ timeList);

            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity() {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }
        });

        player.setVideoDebugListener(this); //for listening to resolution change and  outputing the resolution
    }





    @Override
    public void onRenderedFirstFrame(Surface surface) {
        Log.i("video_f", "Listener-onRenderedFirstFrame..." + surface);


        //Video shows up its first frame -> frame message

    }


    @Override
    public void onVideoEnabled(DecoderCounters counters) {

    }

    @Override
    public void onVideoDecoderInitialized(String decoderName, long initializedTimestampMs, long initializationDurationMs) {

    }

    @Override
    public void onVideoInputFormatChanged(Format format) {

    }

    @Override
    public void onDroppedFrames(int count, long elapsedMs) {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }


    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }
}