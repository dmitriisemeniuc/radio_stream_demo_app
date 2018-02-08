package com.dev.sdv.radiostreamingdemoapp.media.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.dev.sdv.radiostreamingdemoapp.media.MediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.media.MediaPlayerState;
import com.dev.sdv.radiostreamingdemoapp.media.ProgressUpdateListener;
import com.dev.sdv.radiostreamingdemoapp.media.TrackMediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.model.Playlist;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisode;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.utils.Const;

public class MediaService extends Service implements AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.MediaPlayerListener, ProgressUpdateListener {

  public static final String TAG = MediaService.class.getSimpleName();

  public static final String PACKAGE_NAME = "com.dev.sdv.radiostreamingdemoapp.";

  public static final String ACTION_PLAY_TRACK        = PACKAGE_NAME + "playNew";
  public static final String ACTION_RESUME_PLAYBACK   = PACKAGE_NAME + "play";
  public static final String ACTION_PAUSE             = PACKAGE_NAME + "pause";
  public static final String ACTION_PLAY_PLAYLIST     = PACKAGE_NAME + "playPlaylist";
  public static final String ACTION_SEEK_TO           = PACKAGE_NAME + "seekTo";
  public static final String ACTION_STOP_SERVICE      = PACKAGE_NAME + "stopService";

  public static final String PARAM_TRACK_ID = "trackId";

  private AudioManager audioManager;
  private PlaybackState playbackState;
  private MediaSession mediaSession;
  private MediaPlayer mediaPlayer;
  private Track currentTrack;

  private int mediaPlayerState;

  private IBinder mediaServiceBinder = new MediaServiceBinder();

  /******************************
   * Service lifecycle methods
   ******************************/

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "in onCreate");
    audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

    mediaPlayerState = MediaPlayerState.STATE_IDLE;

    // set default playback state
    playbackState = new PlaybackState.Builder()
        .setState(PlaybackState.STATE_NONE, 0, 1.0f)
        .build();

    // setup media session
    mediaSession = new MediaSession(this, MediaService.class.getSimpleName());
    //mediaSession.setMediaButtonReceiver(PendingIntentHelper.getMediaButtonReceiverIntent(this));
    mediaSession.setCallback(new MediaSessionCallback());
    mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS
        | MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
    mediaSession.setActive(true);
    mediaSession.setPlaybackState(playbackState);

    // TODO: add broadcast for headset connect

    // TODO: add wifi lock


  }

  /*
   * @see android.app.Service#onStartCommand(Intent, int, int)
   * */
  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "Media service started");
    if(intent != null && intent.getAction() != null){
      String action = intent.getAction();

      switch (action){
        case ACTION_PLAY_TRACK:{
          int trackId = intent.getIntExtra(PARAM_TRACK_ID, -1);

          if(trackId != -1){
            play(trackId);
          }
          break;
        }
        // TODO: add other actions
      }
    }
    return START_NOT_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return mediaServiceBinder;
    //return null;
  }

  @Override
  public void onRebind(Intent intent) {
    Log.v(TAG, "in onRebind");
    super.onRebind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.v(TAG, "in onUnbind");
    //return true;
    return false;
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "Media service destroyed");
    destroyMediaPlayer();
    super.onDestroy();

  }

  private void destroyMediaPlayer() {

   // TODO: destroy media player

  }

  @Override public void onAudioFocusChange(int i) {
    // TODO:
  }

  @Override public void onProgressUpdate(long progress, long bufferedProgress, long duration) {
    // TODO:
  }

  @Override public void onStateChanged(int state) {
    // TODO:
  }

  public MediaSession.Token getMediaSessionToken() {
    return mediaSession.getSessionToken();
  }

  public static void sendIntent(Context context, String action, int trackId) {
    Intent intent = new Intent(context, MediaService.class);
    intent.setAction(action);

    if (trackId != -1) {
      intent.putExtra(PARAM_TRACK_ID, trackId);
    }
    context.startService(intent);
  }


  public class MediaServiceBinder extends Binder {

    public MediaService getService() {
      return MediaService.this;
    }

    /*public void setListener(Object listener) {
      this.listener = listener;
    }*/
  }

  private final class MediaSessionCallback extends MediaSession.Callback {

    @Override public void onPlayFromSearch(String query, Bundle extras) {
      // TODO:
    }

    @Override public void onSeekTo(long pos) {
      // TODO:
    }

    @Override public void onPlay() {
      // TODO:
    }

    @Override public void onPlayFromUri(Uri uri, Bundle extras) {
      // TODO:
    }

    @Override public void onPause() {
      // TODO:
    }

    @Override public void onFastForward() {
      // TODO:
    }

    @Override public void onRewind() {
      // TODO:
    }

    @Override public void onSkipToNext() {
      // TODO:
    }

    @Override public void onSkipToPrevious() {
      // TODO:
    }
  }

  /******************************
   * Functions for media player control
   ******************************/

  /**
   * Plays a track
   * Will resume from pause if state is paused, loads from scratch otherwise
   * @param trackId id of the track to play
   */

  private void play(int trackId){
    PodcastEpisode podcastEpisode = null;

    // load the track at track id
    if(trackId != -1){
      // TODO: get track by track id
      podcastEpisode = new PodcastEpisode.Builder()
          .id(1)
          .title(Const.PodcastEpisode.DEFAULT_PODCAST_TITLE)
          .subtitle(Const.PodcastEpisode.DEFAULT_PODCAST_SUBTITLE)
          .url(Const.PodcastEpisode.DEFAULT_PODCAST_URL)
          .build();

      Playlist playlist = new Playlist();
      playlist.addToBeginning(String.valueOf(podcastEpisode.getId()));

      // TODO: save play list

      // TODO: if we aren't playing a track

      play(podcastEpisode, true);
    }
  }

  /**
   * Plays a track
   * Will resume from pause if track is null and we are paused
   * @param track to play
   */
  private void play(Track track, boolean playImmediately){
    if(mediaPlayer == null){
      mediaPlayer = new TrackMediaPlayer(this, this, this);
    }

    switch (mediaPlayerState){
      case MediaPlayerState.STATE_IDLE:
      case MediaPlayerState.STATE_CONNECTING:
      case MediaPlayerState.STATE_PLAYING:
      case MediaPlayerState.STATE_PAUSED: {
        // playing a track, true if the track to play is a different one
        if (track != null) {
          // TODO: endUpdateTask();
          // TODO: endPlayback(false);
          startPlayback(track, playImmediately);
        } else if (mediaPlayerState == MediaPlayerState.STATE_PAUSED) {
          mediaPlayer.resumePlayback();
        } else {
          //Timber.w("Player is playing, episode cannot be null");
          Log.w(TAG, "Player is playing, episode cannot be null");
        }
        break;
      }
    }
  }

  /******************************
   * Media Player controls
   ******************************/
  private void startPlayback(Track track, boolean playImmediately) {
    // request audio focus
    int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
        AudioManager.AUDIOFOCUS_GAIN);

    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
      currentTrack = track;
      // TODO: register receiver for headset
      // TODO: wifi lock
      if(currentTrack instanceof PodcastEpisode){
        mediaPlayer.loadTrack(new PodcastEpisode((PodcastEpisode) currentTrack));
      }
      mediaPlayer.startPlayback(playImmediately);
      /*mediaPlayer.setPlaybackSpeed(
          AppPrefHelper.getInstance(this).getPlaybackSpeed(
              mCurrentEpisode.getChannelGeneratedId()));*/
      mediaPlayer.setPlaybackSpeed(-1.0f);
    } else {
      //Timber.d("Audiofocus not granted, result code: %d", result);
      Log.d(TAG, String.format("Audiofocus not granted, result code: %d", result));
    }
  }
}