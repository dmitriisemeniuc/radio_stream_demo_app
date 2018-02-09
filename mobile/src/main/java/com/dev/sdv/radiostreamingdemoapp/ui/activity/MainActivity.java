package com.dev.sdv.radiostreamingdemoapp.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.broadcast.PlayerStateChangeReceiver;
import com.dev.sdv.radiostreamingdemoapp.helper.BroadcastHelper;
import com.dev.sdv.radiostreamingdemoapp.helper.Logger;
import com.dev.sdv.radiostreamingdemoapp.media.MediaControllerCallback;
import com.dev.sdv.radiostreamingdemoapp.media.MediaPlayerState;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.PlayerVisibilityListener;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.PlayerStateChangeListener;
import com.dev.sdv.radiostreamingdemoapp.media.service.MediaService;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisodeModel;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.model.TrackModel;
import com.dev.sdv.radiostreamingdemoapp.ui.view.MiniPlayer;
import com.dev.sdv.radiostreamingdemoapp.utils.SystemUtils;
import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements PlayerVisibilityListener,
    PlayerStateChangeListener {

  public static final String TAG = MainActivity.class.getSimpleName();

  private BottomSheetBehavior behavior;
  private MiniPlayer miniPlayer;
  private MediaController mediaController;
  private MediaService mediaService;
  private MediaServiceConnection mediaServiceConnection;
  private MediaService.MediaServiceBinder mediaServiceBinder;
  private Track track;
  private PlayerStateChangeReceiver playerStateChangeReceiver;

  private boolean mediaServiceBound;

  /* ***********************************************************************************************
   * Lifecycle
   * */

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Verify if permissions are needed
    bindMediaService();
  }

  @Override protected void onStart() {
    super.onStart();

  }

  @Override protected void onResume() {
    super.onResume();
    playerStateChangeReceiver = new PlayerStateChangeReceiver(this);
    LocalBroadcastManager.getInstance(this).registerReceiver(playerStateChangeReceiver,
        new IntentFilter(BroadcastHelper.INTENT_PLAYER_STATE_CHANGE));

    if(mediaServiceBound) updateStateInGui(getState());
  }

  @Override protected void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(playerStateChangeReceiver);
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override protected void onDestroy() {
    stopMediaService();
    super.onDestroy();
  }

  /*
  * END of Lifecycle
  *************************************************************************************************/

  /* ***********************************************************************************************
  * Override methods
  * */
  @Override protected int getLayoutResourceId() {
    return R.layout.activity_main;
  }

  @Override protected void onCreateBase(Bundle savedInstanceState) {
    initViews();
  }

  @Override public void onStateChanged(int state, Track track) {
    if(miniPlayer == null){
      return;
    }

    if(track != null){
      // Changed state of Track
      if(state != PlaybackState.STATE_STOPPED){
        miniPlayer.setTrack(track, state);
      }
    } else {
      miniPlayer.clearMetadata();
    }
  }

  @Override public int getState() {
    if(mediaServiceBound && mediaServiceBinder != null){
      return mediaServiceBinder.getState();
    }
    return MediaPlayerState.STATE_NONE;
  }

  /**
   * Called on Mini Media player show request
   * */
  @Override public boolean onShow() {
    if(behavior != null){
      behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      behavior.setHideable(false);
      return behavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }
    return false;
  }

  /**
  * Called on Mini Media player hide request
  * */
  @Override public boolean onHide() {
    if(behavior != null){
      behavior.setHideable(true);
      behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      return behavior.getState() == BottomSheetBehavior.STATE_HIDDEN;
    }
    return false;
  }

  @Override
  public void onPlayerServiceBoundBase() {
    mediaController = new MediaController(getApplicationContext(), mediaService.getMediaSessionToken());
    mediaController.registerCallback(new MediaControllerCallback(this));
    onStateChanged(getState(), getTrack());
    //onPlayerServiceBound();
  }

  @Override
  public Track getTrack(){
    Track track = null;
    //final AppPrefHelper appPrefHelper = AppPrefHelper.getInstance(this);
    int trackId = -1;

    if (mediaController != null && mediaController.getExtras() != null &&
        mediaController.getExtras().containsKey(MediaService.PARAM_TRACK_ID)) {
      trackId = mediaController.getExtras().getInt(MediaService.PARAM_TRACK_ID);
    }  /*else if (appPrefHelper.getLastPlayedTrackId() != -1) {
      trackId = appPrefHelper.getLastPlayedTrackId();
    }*/

    if (trackId != -1) {
      track = PodcastEpisodeModel.getDefault();
    }
    //return track;
    return PodcastEpisodeModel.getDefault();
  }
  /*
  * END of Override methods
  *************************************************************************************************/

  /* ***********************************************************************************************
  * Other methods
  * */

  private void initViews(){
    initChannelsMenu();
    initBottomSheet();
    setupMiniPlayer();
  }

  private void initChannelsMenu(){
    Logger.d(TAG, "Initialized channels menu");
    // TODO: implement channels menu
  }

  private void initBottomSheet(){
    // Initialize bottom sheet container with mini player inside it
    View bottomSheetView = findViewById(R.id.nsv_bottom_sheet);
    if(bottomSheetView != null){
      behavior = BottomSheetBehavior.from(bottomSheetView);
      Logger.d(TAG, "Initialized Bottom sheet");
      if(behavior != null){
        Logger.d(TAG, "Bottom sheet initial height", behavior.getPeekHeight());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Logger.d(TAG, "Bottom sheet initial state", behavior.getState());
        //behavior.setBottomSheetCallback();
      }
    }
  }

  /**
   * Starts {@link MediaService} service
   * */
  private void bindMediaService(){
    Intent intent = new Intent(this, MediaService.class);
    mediaServiceConnection = new MediaServiceConnection(this);
    bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
    startService(intent);
  }

  /***
   * Stops {@link MediaService} service
   */
  private void stopMediaService(){
    if(SystemUtils.isServiceRunning(MediaService.class, this)){
      Intent intent = new Intent(this, MediaService.class);
      stopService(intent);
      if (mediaServiceBound) {
        unbindService(mediaServiceConnection);
        mediaServiceBound = false;
      }
    }
  }

  /**
   * Configures the mini player
   */
  private void setupMiniPlayer(){
    miniPlayer = new MiniPlayer(this, this);
  }

  /**
   * Playback control methods
   * */
  public void onPlayTrack(){
    Track track = getTrack();
    if(track != null){
      Bundle extras = new Bundle();
      extras.putInt(MediaService.PARAM_TRACK_ID, track.getId());
      //mediaController.getTransportControls().playFromSearch(null, extras);
      String action = MediaService.ACTION_PLAY_TRACK;
      if(getState() == MediaPlayerState.STATE_PAUSED){
        action = MediaService.ACTION_RESUME_PLAYBACK;
      }
      MediaService.sendIntent(this, action, track.getId());
    }
  }

  public void onPauseTrack(){
    Track track = getTrack();
    if(track != null){
      Bundle extras = new Bundle();
      extras.putInt(MediaService.PARAM_TRACK_ID, track.getId());
      //mediaController.getTransportControls().playFromSearch(null, extras);
      MediaService.sendIntent(this, MediaService.ACTION_PAUSE, track.getId());
    }
  }

  public void onStopTrack(){
    Track track = getTrack();
    if(track != null){
      Bundle extras = new Bundle();
      extras.putInt(MediaService.PARAM_TRACK_ID, track.getId());
      //mediaController.getTransportControls().playFromSearch(null, extras);
      MediaService.sendIntent(this, MediaService.ACTION_STOP_SERVICE, track.getId());
    }
  }

  private void updateStateInGui(int state){
    switch (state){
      case MediaPlayerState.STATE_PLAYING:{
        miniPlayer.showPauseButton();
        Logger.d("PLAYING");
        break;
      }
      case MediaPlayerState.STATE_PAUSED:{
        miniPlayer.showPlayButton();
        Logger.d("PAUSED");
        break;
      }
      case MediaPlayerState.STATE_CONNECTING:{
        Logger.d("CONNECTING");
        break;
      }
      case MediaPlayerState.STATE_IDLE:{
        miniPlayer.showPlayButton();
        break;
      }
      case MediaPlayerState.STATE_ENDED:{
        miniPlayer.showPlayButton();
        break;
      }
    }
    Logger.d("updateStateInGui:", "state", String.valueOf(state));
  }
  /*
  * END of Other methods
  * **/

  /* ***********************************************************************************************
   * Broadcast receivers
   * */
  /*private BroadcastReceiver playerStateChangeReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      int state = intent.getIntExtra(BroadcastHelper.EXTRA_PLAYER_STATE, -1);
      String trackId = intent.getStringExtra(BroadcastHelper.EXTRA_TRACK_ID);

      if (state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_PAUSED) {
        track = TrackModel.getTrackById(context, trackId);
        //adapter.notifyDataSetChanged();
      }
    }
  };*/
  /*
  * END of Broadcast receivers
  *************************************************************************************************/

  /* ***********************************************************************************************
   * listeners
   * */

  @Override public void onReceive(Context context, Intent intent) {
    int state = intent.getIntExtra(BroadcastHelper.EXTRA_PLAYER_STATE, -1);
    String trackId = intent.getStringExtra(BroadcastHelper.EXTRA_TRACK_ID);

    updateStateInGui(state);

    if (state == PlaybackState.STATE_PLAYING || state == PlaybackState.STATE_PAUSED) {
      track = TrackModel.getTrackById(context, trackId);
      //adapter.notifyDataSetChanged();
    }
    Logger.d("onReceive:", "state", String.valueOf(state));
  }
  /*
  * END of listeners
  * */

  /* ***********************************************************************************************
  * Inner classes
  * */

  public class MediaServiceConnection implements ServiceConnection {

    private WeakReference<BaseActivity> activity;

    public MediaServiceConnection(BaseActivity activity) {
      this.activity = new WeakReference<>(activity);
    }

    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mediaServiceBinder = (MediaService.MediaServiceBinder) iBinder;
      mediaService = mediaServiceBinder.getService();
      mediaServiceBound = true;

      BaseActivity activity = this.activity.get();

      if (activity == null) {
        return;
      }

      activity.onPlayerServiceBoundBase();
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      if(mediaServiceBound){
        mediaServiceBound = false;
        stopMediaService();
      }
    }
  }
  /*
  * END of Inner classes
  *************************************************************************************************/
}
