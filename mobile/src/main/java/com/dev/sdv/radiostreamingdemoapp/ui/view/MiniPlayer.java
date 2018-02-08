package com.dev.sdv.radiostreamingdemoapp.ui.view;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.helper.PlaybackButtonHelper;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.ControlListener;
import com.dev.sdv.radiostreamingdemoapp.model.PodcastEpisode;
import com.dev.sdv.radiostreamingdemoapp.model.Track;
import com.dev.sdv.radiostreamingdemoapp.ui.activity.MainActivity;
import java.lang.ref.WeakReference;

/**
 * Class represents view wrapper for Mini Player view control.
 * */
public class MiniPlayer implements View.OnClickListener {

  public static final String TAG = MiniPlayer.class.getSimpleName();

  private final WeakReference<MainActivity> activity;
  private final WeakReference<ControlListener> listener;

  private View miniPlayer;
  private View rlControlPlay;
  private View rlControlPause;
  private View rlControlStop;
  private View rlControlFastForward;
  private View rlControlFastRewind;

  private TextView tvTitle;
  private TextView tvSubitle;

  private Track track;

  public MiniPlayer(MainActivity activity, ControlListener listener){
    this.activity = new WeakReference<>(activity);
    this.listener = new WeakReference<>(listener);
    initMiniPlayer();
  }

  /* **********************************************************************************************
  * Initialization
  * */

  private void initMiniPlayer() {
    miniPlayer = this.activity.get().findViewById(R.id.ll_mini_player);
    if(miniPlayer != null){
      rlControlPlay = miniPlayer.findViewById(R.id.rl_mini_player_control_play);
      rlControlPause = miniPlayer.findViewById(R.id.rl_mini_player_control_pause);
      rlControlStop = miniPlayer.findViewById(R.id.rl_mini_player_control_stop);
      rlControlFastForward = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_forward);
      rlControlFastRewind = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_rewind);
      tvTitle = miniPlayer.findViewById(R.id.tv_mini_player_channel_title);
      tvSubitle = miniPlayer.findViewById(R.id.tv_mini_player_track_title);
    }
    setOnClickListeners();
  }

  private void setOnClickListeners(){
    // set Click listeners
    setListener(rlControlPlay);
    setListener(rlControlPause);
    setListener(rlControlStop);
    setListener(rlControlFastForward);
    setListener(rlControlFastRewind);
  }

  private void setListener(View view){
    if(view != null)
      view.setOnClickListener(this);
  }

  /*
  * END of Initialization
  *************************************************************************************************/

  /* ***********************************************************************************************
   * Override methods
   * */
  @Override public void onClick(View v) {
    MainActivity activity = this.activity.get();

    if(activity == null){
      return;
    }

    switch (v.getId()){
      case R.id.rl_mini_player_control_play:
        Log.d(TAG, "pressed play");
        activity.onPlayTrack();
        showPauseButton();
        break;
      case R.id.rl_mini_player_control_pause:
        Log.d(TAG, "pressed pause");
        activity.onPauseTrack();
        showPlayButton();
        break;
      case R.id.rl_mini_player_control_stop:
        Log.d(TAG, "pressed stop");
        showPlayButton();
        break;
      case R.id.rl_mini_player_control_fast_forward:
        Log.d(TAG, "pressed  fast forward");
        break;
      case R.id.rl_mini_player_control_fast_rewind:
        Log.d(TAG, "pressed  fast rewind");
        break;
      default:
        Log.d(TAG, "unknown button pressed");
    }
  }
  /*
   * END of Override methods
   * */

  /* ***********************************************************************************************
   * Other methods
   * */
  public void setTitle(String title){
    if(!TextUtils.isEmpty(title) && tvTitle != null){
      tvTitle.setText(title);
    }
  }

  public void setSubtitle(String subtitle){
    if(!TextUtils.isEmpty(subtitle) && tvSubitle != null){
      tvSubitle.setText(subtitle);
    }
  }

  // TODO: add logo and favorites

  private void showPauseButton(){
    rlControlPlay.setVisibility(View.GONE);
    rlControlPause.setVisibility(View.VISIBLE);
    miniPlayer.invalidate();
  }

  private void showPlayButton(){
    rlControlPause.setVisibility(View.GONE);
    rlControlPlay.setVisibility(View.VISIBLE);
    miniPlayer.invalidate();
  }

  public void clearMetadata(){
    if(tvTitle != null){
      tvTitle.setText(null);
    }
    if(tvSubitle != null){
      tvSubitle.setText(null);
    }
    // TODO: hide logo and track duration
  }

  private boolean showPlayer(){
    if(listener.get() != null){
      return listener.get().onShow();
    }
    return false;
  }

  private boolean hidePlayer(){
    if(listener.get() != null){
      return listener.get().onHide();
    }
    return false;
  }

  public void setTrack(Track track, int state) {
    this.track = track;
    // TODO: set logo
    miniPlayer.setTag(state);
    tvTitle.setText(track.getTitle());
    // TODO: set duration
    // TODO: set time left
    // TODO: set progress
    showPlayer();
  }

  /*
  * END of other methods
  *************************************************************************************************/
}
