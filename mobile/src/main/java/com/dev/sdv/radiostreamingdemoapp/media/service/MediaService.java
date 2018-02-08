package com.dev.sdv.radiostreamingdemoapp.media.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.dev.sdv.radiostreamingdemoapp.media.MediaPlayerState;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.ProgressUpdateListener;
import com.dev.sdv.radiostreamingdemoapp.media.player.MediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.media.player.TrackMediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.model.Playlist;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisode;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.utils.Const;

public class MediaService extends Service implements AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.MediaPlayerListener, ProgressUpdateListener {

  public static final String TAG = MediaService.class.getSimpleName();

  public static final String PACKAGE_NAME = "com.dev.sdv.radiostreamingdemoapp.";

  public static final String ACTION_PLAY_TRACK = PACKAGE_NAME + "playNew";
  public static final String ACTION_RESUME_PLAYBACK = PACKAGE_NAME + "play";
  public static final String ACTION_PAUSE = PACKAGE_NAME + "pause";
  public static final String ACTION_PLAY_PLAYLIST = PACKAGE_NAME + "playPlaylist";
  public static final String ACTION_SEEK_TO = PACKAGE_NAME + "seekTo";
  public static final String ACTION_STOP_SERVICE = PACKAGE_NAME + "stopService";

  public static final String PARAM_TRACK_ID = "trackId";

  private AudioManager audioManager;
  private PlaybackState playbackState;
  private MediaSession mediaSession;
  private MediaPlayer mediaPlayer;
  private Track currentTrack;

  private int mediaPlayerState;

  private IBinder mediaServiceBinder = new MediaServiceBinder();

  /* ***********************************************************************************************
   * static
   * */

  public static void sendIntent(Context context, String action, int trackId) {
    Intent intent = new Intent(context, MediaService.class);
    intent.setAction(action);

    if (trackId != -1) {
      intent.putExtra(PARAM_TRACK_ID, trackId);
    }
    context.startService(intent);
  }

  /*
   * END of static
   * **********************************************************************************************/

  /* ***********************************************************************************************
   * Service lifecycle
   */

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
    //mediaSession.setCallback(new MediaSessionCallback());
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

    handleIntent(intent);

    return START_NOT_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return mediaServiceBinder;
    //return null;
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "Media service destroyed");
    destroyMediaPlayer();
    super.onDestroy();
  }

   /*
   * END of Service lifecycle
   ************************************************************************************************/

  /* ***********************************************************************************************
  * Other methods
  * */

  private void handleIntent(Intent intent) {
    if (intent == null || intent.getAction() == null) {
      return;
    }

    switch (intent.getAction()) {
      case ACTION_PLAY_TRACK: {
        int trackId = intent.getIntExtra(PARAM_TRACK_ID, -1);

        if (trackId != -1) {
          play(trackId);
        }
        break;
      }
      case ACTION_PAUSE: {
        pause();
        break;
      }
      // TODO: add other actions
    }
  }

  private void destroyMediaPlayer() {

    // TODO: destroy media player

  }

  public MediaSession.Token getMediaSessionToken() {
    return mediaSession.getSessionToken();
  }

  /*
  * END of other methods
  * ***********************************************************************************************/

  /* ***********************************************************************************************
  *  listeners
  * */

  @Override public void onAudioFocusChange(int i) {
    Log.d(TAG, String.format("onAudioFocusChange", i));
    // TODO:
  }

  @Override public void onProgressUpdate(long progress, long bufferedProgress, long duration) {
    Log.d(TAG, String.format("onProgressUpdate: progress %d, bufferedProgress %d, duration %d",
        progress, bufferedProgress, duration));
    // TODO:
  }

  @Override public void onStateChanged(int state) {
    Log.d(TAG, String.format("onStateChanged: ", state));
    mediaPlayerState = state;
  }

  /*
  * END of listeners
  *************************************************************************************************/


  /* ***********************************************************************************************
   * Incoming commands handling
   */

  /**
   * Plays a track
   * Will resume from pause if state is paused, loads from scratch otherwise
   *
   * @param trackId id of the track to play
   */
  private void play(int trackId) {
    PodcastEpisode podcastEpisode;

    // load the track at track id
    if (trackId != -1) {
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
   *
   * @param track to play
   */
  private void play(Track track, boolean playImmediately) {
    if (mediaPlayer == null) {
      mediaPlayer = new TrackMediaPlayer(this, this, this);
    }

    switch (mediaPlayerState) {
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
          resumePlayback();
        } else {
          //Timber.w("Player is playing, episode cannot be null");
          Log.w(TAG, "Player is playing, episode cannot be null");
        }
        break;
      }
    }
  }

  /**
   * Will pause the player if it's playing
   */
  private void pause() {

    switch (mediaPlayerState) {
      case MediaPlayerState.STATE_PLAYING:
        // we paused, resume playing state
        pausePlayback();
        break;
      default:
        //Timber.w("Trying to pause an track, but player is in state: %s", playbackState);
        Log.w(TAG,
            String.format("Trying to pause an track, but player is in state: %s", playbackState));
        break;
    }
  }

  /*
   * END of Incoming commands handling
   *************************************************************************************************

  /* ***********************************************************************************************
   * Media Player controls
   */
  private void startPlayback(Track track, boolean playImmediately) {
    // request audio focus
    int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
        AudioManager.AUDIOFOCUS_GAIN);

    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
      currentTrack = track;
      // TODO: register receiver for headset
      // TODO: wifi lock
      if (currentTrack instanceof PodcastEpisode) {
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



  private void pausePlayback() {
    mediaPlayer.pausePlayback();
  }

  private void resumePlayback() {
    mediaPlayer.resumePlayback();
  }

  /*
   * END of Media Player controls
   ************************************************************************************************/

  /* ***********************************************************************************************
  * Inner classes
  * */

  public class MediaServiceBinder extends Binder {

    public MediaService getService() {
      return MediaService.this;
    }

    /*public void setListener(Object listener) {
      this.listener = listener;
    }*/
  }

  /*
  * END of Inner classes
  *************************************************************************************************/
}
