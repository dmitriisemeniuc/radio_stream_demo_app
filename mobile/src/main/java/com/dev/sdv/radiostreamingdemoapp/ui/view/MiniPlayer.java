package com.dev.sdv.radiostreamingdemoapp.ui.view;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.ui.activity.MainActivity;
import java.lang.ref.WeakReference;

public class MiniPlayer implements View.OnClickListener {

  public static final String TAG = MiniPlayer.class.getSimpleName();

  private final WeakReference<MainActivity> activity;
  private View miniPlayer;
  private View playerControlPlay;
  private View playerControlPause;
  private View playerControlStop;
  private View playerControlFastForward;
  private View playerControlFastRewind;
  private View playerChannelTitle;
  private View playerTrackTitle;

  public MiniPlayer(MainActivity activity){
    this.activity = new WeakReference<>(activity);
    initMiniPlayer();
  }

  private void initMiniPlayer() {
    miniPlayer = this.activity.get().findViewById(R.id.ll_mini_player);
    playerControlPlay = miniPlayer.findViewById(R.id.rl_mini_player_control_play);
    playerControlPause = miniPlayer.findViewById(R.id.rl_mini_player_control_pause);
    playerControlStop = miniPlayer.findViewById(R.id.rl_mini_player_control_stop);
    playerControlFastForward = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_forward);
    playerControlFastRewind = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_rewind);
    playerChannelTitle = miniPlayer.findViewById(R.id.tv_mini_player_channel_title);
    playerTrackTitle = miniPlayer.findViewById(R.id.tv_mini_player_track_title);

    // set Click listeners
    if(playerControlPlay != null)
      playerControlPlay.setOnClickListener(this);
    if(playerControlPause != null)
      playerControlPause.setOnClickListener(this);
    if(playerControlStop != null)
      playerControlStop.setOnClickListener(this);
    if(playerControlFastForward != null)
      playerControlFastForward.setOnClickListener(this);
    if(playerControlFastRewind != null)
      playerControlFastRewind.setOnClickListener(this);
    if(playerControlFastRewind != null)
      playerControlFastRewind.setOnClickListener(this);
  }

  @Override public void onClick(View v) {
    switch (v.getId()){
      case R.id.rl_mini_player_control_play:
        Log.d(TAG, "Mini player: play");
        break;
      case R.id.rl_mini_player_control_pause:
        Log.d(TAG, "Mini player: pause");
        break;
      case R.id.rl_mini_player_control_stop:
        Log.d(TAG, "Mini player: stop");
        break;
      case R.id.rl_mini_player_control_fast_forward:
        Log.d(TAG, "Mini player: fast forward");
        break;
      case R.id.rl_mini_player_control_fast_rewind:
        Log.d(TAG, "Mini player: fast rewind");
        break;
        default:
          Log.d(TAG, "Mini player: unknown button pressed");
    }
  }
}
