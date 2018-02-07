package com.dev.sdv.radiostreamingdemoapp.ui.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.BottomSheetBehavior;
import android.util.Log;
import android.view.View;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.media.service.MediaService;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisode;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.ui.view.MiniPlayer;
import com.dev.sdv.radiostreamingdemoapp.utils.SystemUtils;

public class MainActivity extends BaseActivity implements MiniPlayer.ControlListener {

  private BottomSheetBehavior behavior;
  private MiniPlayer miniPlayer;
  private MediaService.MediaServiceBinder mediaServiceBinder;
  private MediaService mediaService;
  private boolean mediaServiceBound;

  // Lifecycle methods

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override protected void onStart() {
    super.onStart();
  }

  @Override protected void onResume() {
    super.onResume();
    // Verify if permissions are needed
    bindMediaService();
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

  @Override protected void onStateChanged(int state, Track track) {
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
    bindService(intent, mediaServiceConnection, Context.BIND_AUTO_CREATE);
    startService(intent);
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

  // Media Service Binding

  private ServiceConnection mediaServiceConnection = new ServiceConnection() {
    @Override public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      mediaServiceBinder = (MediaService.MediaServiceBinder) iBinder;
      mediaService = mediaServiceBinder.getService();
      mediaServiceBound = true;
    }

    @Override public void onServiceDisconnected(ComponentName componentName) {
      if(mediaServiceBound){
        mediaServiceBound = false;
        stopMediaService();
      }
    }
  };
}
