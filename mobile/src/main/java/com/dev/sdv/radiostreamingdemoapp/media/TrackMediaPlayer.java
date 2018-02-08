package com.dev.sdv.radiostreamingdemoapp.media;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import java.io.File;

public class TrackMediaPlayer extends MediaPlayer implements ProgressUpdateListener, ExoPlayer.EventListener {

  private static final String TAG = TrackMediaPlayer.class.getSimpleName();
  private static final int MAX_CACHE_SIZE = 250_000_000;
  private static final String TRACK_CACHE_DIR = "podcast-cache";

  private final Context context;
  private final Handler handler;
  private final SimpleExoPlayer exoPlayer;
  private Runnable progressUpdater;
  private Track track;

  private int mediaPlayerState;
  private boolean isStreaming;
  private boolean isUpdatingProgress;

  public TrackMediaPlayer(MediaPlayerListener mediaPlayerListener,
      ProgressUpdateListener progressUpdateListener, Context context) {
    super(mediaPlayerListener, progressUpdateListener);
    this.context = context;
    this.handler = new Handler();
    TrackSelector trackSelector = new DefaultTrackSelector(this.handler);
    DefaultLoadControl loadControl = new DefaultLoadControl();
    exoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector, loadControl);
    exoPlayer.addListener(this);
    mediaPlayerState = MediaPlayerState.STATE_IDLE;
    progressUpdater = new ProgressUpdater();
  }

  @Override public void onProgressUpdate(long progress, long bufferedProgress, long duration) {
    progressUpdateListener.onProgressUpdate(progress,
        isStreaming() ? bufferedProgress : duration, duration);
  }

  @Override public void loadTrack(Track track) {
    this.track = track;
  }

  @Override public void startPlayback(boolean playImmediately) {
   /* if (track.getProgress() > -1) {
      exoPlayer.seekTo(track.getProgress());
    } else {*/
      exoPlayer.seekTo(0);
    //}
    MediaSource mMediaSource = buildMediaSource();
    exoPlayer.prepare(mMediaSource, false, false);
    exoPlayer.setPlayWhenReady(playImmediately);
  }

  @Override public void resumePlayback() {
    exoPlayer.setPlayWhenReady(true);
  }

  @Override public void pausePlayback() {
    exoPlayer.setPlayWhenReady(false);
  }

  @Override public void stopPlayback() {
    exoPlayer.stop();
    isStreaming = false;
    track = null;
  }

  @Override public void seekTo(long position) {
    exoPlayer.seekTo(position);
  }

  @Override public boolean isStreaming() {
    return isStreaming;
  }

  @Override public int getState() {
    return mediaPlayerState;
  }

  @Override public long getCurrentPosition() {
    return exoPlayer.getCurrentPosition();
  }

  @Override public long getDuration() {
    return exoPlayer.getDuration();
  }

  @Override public long getBufferedPosition() {
    return exoPlayer.getBufferedPosition();
  }

  @Override public void setPlaybackSpeed(float speed) {
    // TODO: check
    /*PlaybackParams playbackParams = new PlaybackParams();
    playbackParams.setSpeed(speed);
    exoPlayer.setPlaybackParams(playbackParams);*/
  }

  @Override public void onLoadingChanged(boolean isLoading) {

  }

  @Override public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
    String playbackStateStr;

    switch (playbackState) {
      case ExoPlayer.STATE_BUFFERING:
        mediaPlayerState = MediaPlayerState.STATE_CONNECTING;
        playbackStateStr = "Buffering";
        break;
      case ExoPlayer.STATE_ENDED:
        mediaPlayerState = MediaPlayerState.STATE_ENDED;
        playbackStateStr = "Ended";
        break;
      case ExoPlayer.STATE_IDLE:
        mediaPlayerState = MediaPlayerState.STATE_IDLE;
        playbackStateStr = "Idle";
        break;
      case ExoPlayer.STATE_READY:
        mediaPlayerState = playWhenReady ? MediaPlayerState.STATE_PLAYING :
            MediaPlayerState.STATE_PAUSED;
        playbackStateStr = "Ready";

        if (playWhenReady) {
          startProgressUpdater();
        } else {
          stopProgressUpdater();
        }
        break;
      default:
        mediaPlayerState = MediaPlayerState.STATE_IDLE;
        playbackStateStr = "Unknown";
        break;
    }
    mediaPlayerListener.onStateChanged(mediaPlayerState);
    Log.d(TAG, String.format("ExoPlayer state changed: %s, Play When Ready: %s",
        playbackStateStr,
        String.valueOf(playWhenReady)));
  }

  @Override public void onTimelineChanged(Timeline timeline, Object manifest) {

  }

  @Override public void onPlayerError(ExoPlaybackException error) {
    Log.w(TAG, "Player error encountered", error);
    stopPlayback();
  }

  @Override public void onPositionDiscontinuity() {

  }

  private MediaSource buildMediaSource() {
    DataSource.Factory dataSourceFactory = null;
    Uri uri = null;

    // return the uri to play
    uri = Uri.parse(track.getUrl());
    dataSourceFactory = getCacheDataSource(
        new File(context.getCacheDir(), TRACK_CACHE_DIR),
        context.getString(R.string.user_agent));
    isStreaming = true;

    if (uri != null) {
      Log.d(TAG, "Playing from URI " + uri);
      return new ExtractorMediaSource(uri, dataSourceFactory, new AudioExtractorsFactory(),
          handler, null);
    }
    throw new IllegalStateException("Unable to build media source");
  }

  private static CacheDataSourceFactory getCacheDataSource(File cacheDir, String userAgent) {
    Cache cache = new SimpleCache(cacheDir, new LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE));
    DataSource.Factory upstream = new DefaultHttpDataSourceFactory(userAgent, null,
        DefaultHttpDataSource.DEFAULT_CONNECT_TIMEOUT_MILLIS,
        DefaultHttpDataSource.DEFAULT_READ_TIMEOUT_MILLIS, true);
    return new CacheDataSourceFactory(cache, upstream,
        CacheDataSource.FLAG_CACHE_UNBOUNDED_REQUESTS,
        CacheDataSource.DEFAULT_MAX_CACHE_FILE_SIZE);
  }

  private class ProgressUpdater implements Runnable {

    private static final int TIME_UPDATE_MS = 16;

    @Override
    public void run() {
      long progress = exoPlayer.getCurrentPosition();
      long duration = exoPlayer.getDuration();
      progressUpdateListener.onProgressUpdate(progress, 0, duration);
      handler.postDelayed(progressUpdater, TIME_UPDATE_MS);
    }
  }

  private void startProgressUpdater() {

    if (!isUpdatingProgress) {
      progressUpdater.run();
      isUpdatingProgress = true;
    }
  }

  private void stopProgressUpdater() {

    if (isUpdatingProgress) {
      handler.removeCallbacks(progressUpdater);
      isUpdatingProgress = false;
    }
  }
}
