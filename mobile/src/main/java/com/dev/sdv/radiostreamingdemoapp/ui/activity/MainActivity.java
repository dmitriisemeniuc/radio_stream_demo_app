package com.dev.sdv.radiostreamingdemoapp.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.media.MediaControllerCallback;
import com.dev.sdv.radiostreamingdemoapp.media.service.MediaService;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisodeModel;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.ui.view.MiniPlayer;
import com.dev.sdv.radiostreamingdemoapp.utils.SystemUtils;
import java.lang.ref.WeakReference;

public class MainActivity extends BaseActivity implements MiniPlayer.ControlListener {

  private BottomSheetBehavior behavior;
  private MiniPlayer miniPlayer;
  private MediaController mediaController;
  private MediaService.MediaServiceBinder mediaServiceBinder;
  private MediaService mediaService;
  private MediaServiceConnection mediaServiceConnection;
  private boolean mediaServiceBound;

  // Lifecycle methods

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
  }

  @Override protected void onPause() {
    super.onPause();
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override protected void onDestroy() {
    super.onDestroy();
    stopMediaService();
  }

  // Override methods

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
        // Changed state of PodcastEpisode
        if(state != PlaybackState.STATE_STOPPED){
          miniPlayer.setTrack(track, state);
        }
    } else {
      miniPlayer.clearMetadata();
    }
  }

  @Override public int getState() {
    int state = PlaybackState.STATE_NONE;

    if (mediaController != null && mediaController.getPlaybackState() != null) {
      state = mediaController.getPlaybackState().getState();
    }
    return state;
  }

  // Other methods

  private void initViews(){
    initChannelsMenu();
    initBottomSheet();
    setupMiniPlayer();
  }

  private void initChannelsMenu(){
    Log.d(TAG, "Initialized channels menu");
    // TODO: implement channels menu
  }

  private void initBottomSheet(){
    // Initialize bottom sheet container with mini player inside it
    View bottomSheetView = findViewById(R.id.nsv_bottom_sheet);
    if(bottomSheetView != null){
      behavior = BottomSheetBehavior.from(bottomSheetView);
      Log.d(TAG, "Initialized Bottom sheet");
      if(behavior != null){
        Log.d(TAG, String.format("Bottom sheet initial height: %d", behavior.getPeekHeight()));
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        Log.d(TAG, String.format("Bottom sheet initial state: %s", behavior.getState()));
        //behavior.setBottomSheetCallback();
      }
    }
  }

  private void bindMediaService(){
    Intent intent = new Intent(this, MediaService.class);
    mediaServiceConnection = new MediaServiceConnection(this);
    bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
    startService(intent);
  }

  @Override
  public void onPlayerServiceBoundBase() {
    mediaController = new MediaController(getApplicationContext(), mediaService.getMediaSessionToken());
    mediaController.registerCallback(new MediaControllerCallback(this));
    onStateChanged(getState(), getTrack());
    //onPlayerServiceBound();
  }

  /**
   * Configures the mini player
   */
  private void setupMiniPlayer(){
    miniPlayer = new MiniPlayer(this, this);
  }

  @Override public boolean onShow() {
    if(behavior != null){
      behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
      behavior.setHideable(false);
      return behavior.getState() == BottomSheetBehavior.STATE_EXPANDED;
    }
    return false;
  }

  @Override public boolean onHide() {
    if(behavior != null){
      behavior.setHideable(true);
      behavior.setState(BottomSheetBehavior.STATE_HIDDEN);
      return behavior.getState() == BottomSheetBehavior.STATE_HIDDEN;
    }
    return false;
  }

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
  /**
   * Playback control methods
   * */
  public void onPlayTrack(){
    Track track = getTrack();
    if(track != null){
      Bundle extras = new Bundle();
      extras.putInt(MediaService.PARAM_TRACK_ID, track.getId());
      mediaController.getTransportControls().playFromSearch(null, extras);
      MediaService.sendIntent(this, MediaService.ACTION_PLAY_TRACK, track.getId());
    }
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
}
