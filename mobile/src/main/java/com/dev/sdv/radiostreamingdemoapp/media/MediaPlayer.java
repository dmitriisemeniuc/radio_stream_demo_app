package com.dev.sdv.radiostreamingdemoapp.media;

import com.dev.sdv.radiostreamingdemoapp.model.Track;

public abstract class MediaPlayer {

  protected MediaPlayerListener mediaPlayerListener;
  protected ProgressUpdateListener progressUpdateListener;

  public MediaPlayer(MediaPlayerListener mediaPlayerListener,
      ProgressUpdateListener progressUpdateListener) {
    this.mediaPlayerListener = mediaPlayerListener;
    this.progressUpdateListener = progressUpdateListener;
  }

  public void tearDown() {
    mediaPlayerListener = null;
    progressUpdateListener = null;
  }

  public abstract void loadTrack(Track track);

  public abstract void startPlayback(boolean playImmediately);

  public abstract void resumePlayback();

  public abstract void pausePlayback();

  public abstract void stopPlayback();

  public abstract void seekTo(long position);

  public abstract boolean isStreaming();

  public abstract int getState();

  public abstract long getCurrentPosition();

  public abstract long getDuration();

  public abstract long getBufferedPosition();

  public abstract void setPlaybackSpeed(float speed);

  /**
   * Used to listen for media player state changes
   */
  public interface MediaPlayerListener {

    void onStateChanged(int state);

  }
}
