package com.dev.sdv.radiostreamingdemoapp.media.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.dev.sdv.radiostreamingdemoapp.helper.BroadcastHelper;
import com.dev.sdv.radiostreamingdemoapp.helper.Logger;
import com.dev.sdv.radiostreamingdemoapp.media.MediaPlayerState;
import com.dev.sdv.radiostreamingdemoapp.media.TrackStatus;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.ProgressUpdateListener;
import com.dev.sdv.radiostreamingdemoapp.media.player.MediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.media.player.TrackMediaPlayer;
import com.dev.sdv.radiostreamingdemoapp.model.Playlist;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisode;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.model.TrackModel;
import com.dev.sdv.radiostreamingdemoapp.utils.Const;

public class MediaService extends Service implements AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.MediaPlayerListener, ProgressUpdateListener {

  public static final String TAG = MediaService.class.getSimpleName();

  private static final int MS_TO_REVERSE_ON_PAUSE   = 0;

  public static final String ACTION_PLAY_TRACK      = "com.dev.sdv.radiostreamingdemoapp.playNew";
  public static final String ACTION_RESUME_PLAYBACK = "com.dev.sdv.radiostreamingdemoapp.play";
  public static final String ACTION_PAUSE           = "com.dev.sdv.radiostreamingdemoapp.pause";
  public static final String ACTION_PLAY_PLAYLIST   = "com.dev.sdv.radiostreamingdemoapp.playPlaylist";
  public static final String ACTION_SEEK_TO         = "com.dev.sdv.radiostreamingdemoapp.seekTo";
  public static final String ACTION_STOP_SERVICE    = "com.dev.sdv.radiostreamingdemoapp.stopService";

  public static final String PARAM_TRACK_ID = "trackId";

  private AudioManager  audioManager;
  private PlaybackState playbackState;
  private MediaSession  mediaSession;
  private MediaPlayer   mediaPlayer;
  private Track         currentTrack;

  private int           mediaPlayerState;
  private boolean       serviceBound;

  private IBinder       mediaServiceBinder = new MediaServiceBinder();

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
    Logger.d(TAG, "in onCreate");
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

    handleIntent(intent);

    return START_NOT_STICKY;
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    serviceBound = true;
    return mediaServiceBinder;
    //return null;
  }

  @Override public boolean onUnbind(Intent intent) {

    if (mediaPlayerState == MediaPlayerState.STATE_IDLE && !(mediaPlayer instanceof TrackMediaPlayer)) {
      stopSelf();
    }
    serviceBound = false;
    return super.onUnbind(intent);
  }

  @Override
  public void onDestroy() {
    Logger.d(TAG, "Media service destroyed");
    destroyMediaPlayer();
    mediaSession.release();
    //releaseWifiLock();
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

    Logger.d(TAG, "handleIntent");

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
      case ACTION_RESUME_PLAYBACK:

        if (mediaPlayerState != MediaPlayerState.STATE_PLAYING) {
          play(-1);
        }
        break;
      case ACTION_STOP_SERVICE:{
        Logger.d(TAG, "handleIntent:", "stop service", "lalala", "filmix", "tiande");
        endPlayback(true);

        if (!serviceBound) {
          stopSelf();
        }
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
  private void updateTrack(int state){
    Logger.d(TAG, "Updating track, state", state);

    if (mediaPlayer == null || currentTrack == null || currentTrack.getId() == -1) {
      return;
    }

    switch (state) {
      case MediaPlayerState.STATE_PLAYING:
        // mark episode as playing in DB
        currentTrack.setStatus(TrackStatus.IN_PROGRESS);
        break;
      case MediaPlayerState.STATE_PAUSED:
      case MediaPlayerState.STATE_IDLE:
        currentTrack.setStatus(TrackStatus.PLAYED);
        currentTrack.setProgress(mediaPlayer.getCurrentPosition() - MS_TO_REVERSE_ON_PAUSE);
        break;
      default:
        throw new IllegalArgumentException(
            "Incorrect state for showing play pause notification");
    }
    /*AppPrefHelper appPrefHelper = AppPrefHelper.getInstance(this);
    appPrefHelper.setLastPlayedEpisodeId(mCurrentEpisode.getId());*/

    Bundle params = new Bundle();
    params.putLong(TrackModel.PARAM_TRACK_PROGRESS, currentTrack.getProgress());
    params.putInt(TrackModel.PARAM_TRACK_STATUS, currentTrack.getStatus());
    //TrackModel.updateEpisodeAsync(this, mCurrentEpisode.getId(), params);
  }
  /* ***********************************************************************************************
  * Track Updates
  * */

  /*
   * END of Track Updates
   ************************************************************************************************/

  /* ***********************************************************************************************
  *  listeners
  * */

  @Override public void onAudioFocusChange(int focusChange) {
    Logger.d(TAG, "onAudioFocusChange", focusChange);
    //Timber.d("AudioFocusChange, result code: %d", focusChange);
    /*boolean pauseOnNotification = UserPrefHelper.get(this).getBoolean(
        R.string.pref_key_pause_playback_during_notification);*/

    switch (focusChange) {
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:

        /*if (pauseOnNotification) {
          // record the playing before focus change value
          mPlayingBeforeFocusChange = getPlaybackState() == MediaPlayerState.STATE_PLAYING;
          pause();
        } else {
          mStreamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
          mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
              (int) (mStreamVolume * AUDIO_DUCK), 0);
        }
        break;
      case AudioManager.AUDIOFOCUS_LOSS:
      case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
        // record the playing before focus change value
        mPlayingBeforeFocusChange = mMediaPlayerState == MediaPlayerState.STATE_PLAYING;
        pause();
        break;
      case AudioManager.AUDIOFOCUS_GAIN:

        if (mStreamVolume > -1) {
          mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mStreamVolume, 0);
          mStreamVolume = -1;
        }

        // gained focus, start playback if we were playing before the focus change
        if (mPlayingBeforeFocusChange && pauseOnNotification) {
          mPlayingBeforeFocusChange = false;
          play(null, true);
        }
        break;*/
    }
  }

  /**
   * Media Player interface callbacks
   * */
  @Override public void onProgressUpdate(long progress, long bufferedProgress, long duration) {
    Logger.d(TAG, String.format("onProgressUpdate: progress %d, bufferedProgress %d, duration %d",
        progress, bufferedProgress, duration));
    //BroadcastHelper.broadcastProgressUpdate(this, progress, bufferedProgress, left);
  }

  @Override public void onStateChanged(int state) {
    Logger.d(TAG, String.format("onStateChanged: ", state));
    mediaPlayerState = state;
    switch (state) {
      case MediaPlayerState.STATE_CONNECTING:
        break;
      case MediaPlayerState.STATE_ENDED:
        /*endUpdateTask();
        endPlayback(true);
        finishEpisode();
        playNextEpisode();*/
        break;
      case MediaPlayerState.STATE_IDLE:
        updateTrack(state);
        //endUpdateTask();
        break;
      case MediaPlayerState.STATE_PLAYING:
        updateTrack(state);
        //startNotificationUpdate();
        //startUpdateTask();
        break;
      case MediaPlayerState.STATE_PAUSED:
        //endUpdateTask();
        updateTrack(state);
        //startNotificationUpdate();
        break;
    }
    //updateWidget();
    //String serverId = currentTrack != null ? currentTrack.getGeneratedId() : "";
    String trackId = currentTrack != null ? String.valueOf(currentTrack.getId()) : "";
    BroadcastHelper.broadcastPlayerStateChange(this, mediaPlayerState, trackId);
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
    Track track = null;

    // load the track at track id
    if (trackId != -1) {
      // TODO: get track by track id
      track = new PodcastEpisode.Builder()
          .id(1)
          .title(Const.Track.DEBUG_PODCAST_TITLE)
          .subtitle(Const.Track.DEBUG_PODCAST_SUBTITLE)
          .url(Const.Track.DEBUG_PODCAST_URL)
          .build();

      Playlist playlist = new Playlist();
      playlist.addToBeginning(String.valueOf(track.getId()));

      // TODO: save play list

      // TODO: if we aren't playing a track
    }
    play(track, true);
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
      case MediaPlayerState.STATE_CONNECTING:
      case MediaPlayerState.STATE_PLAYING:
      case MediaPlayerState.STATE_PAUSED:

        // playing an track, true if the track to play is a different one
        if (track != null) {
          //endUpdateTask();
          endPlayback(false);
          startPlayback(track, playImmediately);
        } else if (mediaPlayerState == MediaPlayerState.STATE_PAUSED) {
          mediaPlayer.resumePlayback();
        } else {
          Logger.w("Player is playing, track cannot be null");
        }
        break;
      case MediaPlayerState.STATE_ENDED:
      case MediaPlayerState.STATE_IDLE:
        // stopped or uninitialized, so we need to start from scratch
        if (track != null) {
          startPlayback(track, playImmediately);
        } else {
          Logger.w("Player is stopped/uninitialized, track cannot be null");
        }
        break;
      default:
        Logger.w(TAG,"Trying to play an track, but player is in state:", mediaPlayerState);
        break;
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
        Logger.w(TAG,
            String.format("Trying to pause an track, but player is in state: %s", playbackState));
        break;
    }
  }

  private void endPlayback(boolean cancelNotification) {
    updateTrack(MediaPlayerState.STATE_IDLE);
    // TODO: unregisterReceivers headset receiver;

    /*if (cancelNotification) {
      mediaNotificationManager.stopNotification();
    }*/
    audioManager.abandonAudioFocus(this);
    stopPlayback();
    //releaseWifiLock();
  }

  /*
   * END of Incoming commands handling
   *************************************************************************************************

  /* ***********************************************************************************************
   * Media Player controls
   */
  private void startPlayback(com.dev.sdv.radiostreamingdemoapp.model.Track track, boolean playImmediately) {
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
      Logger.d(TAG, String.format("Audiofocus not granted, result code: %d", result));
    }
  }



  private void pausePlayback() {
    mediaPlayer.pausePlayback();
  }

  private void resumePlayback() {
    mediaPlayer.resumePlayback();
  }

  private void stopPlayback(){
    mediaPlayer.stopPlayback();
  }

  /*
   * END of Media Player controls
   ************************************************************************************************/

  /* ***********************************************************************************************
  *
  * */



  /*
  *
  *************************************************************************************************/

  /* ***********************************************************************************************
  * Inner classes
  * */

  public class MediaServiceBinder extends Binder {

    public MediaService getService() {
      return MediaService.this;
    }

    public int getState(){
      return mediaPlayerState;
    }

    /*public void setListener(Object listener) {
      this.listener = listener;
    }*/
  }

  /*
  * END of Inner classes
  *************************************************************************************************/
}
