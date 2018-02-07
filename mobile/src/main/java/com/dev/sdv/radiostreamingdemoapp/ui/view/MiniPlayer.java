package com.dev.sdv.radiostreamingdemoapp.ui.view;

import android.media.session.PlaybackState;
import android.support.design.widget.BottomSheetBehavior;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import com.dev.sdv.radiostreamingdemoapp.R;
import com.dev.sdv.radiostreamingdemoapp.helper.PlaybackButtonHelper;
import com.dev.sdv.radiostreamingdemoapp.model.Episode;
import com.dev.sdv.radiostreamingdemoapp.ui.activity.MainActivity;
import java.lang.ref.WeakReference;

public class MiniPlayer implements View.OnClickListener {

  public static final String TAG = MiniPlayer.class.getSimpleName();

  private final WeakReference<MainActivity> activity;
  private final WeakReference<ControlListener> listener;
  private View miniPlayer;
  private ImageView ivControlPlayback;
  private View rlControlPlayback;
  private View rlControlStop;
  private View rlControlFastForward;
  private View rlControlFastRewind;
  private TextView tvTitle;
  private TextView tvSubitle;

  private Episode episode;

  public MiniPlayer(MainActivity activity, ControlListener listener){
    this.activity = new WeakReference<>(activity);
    this.listener = new WeakReference<>(listener);
    initMiniPlayer();
  }

  private void initMiniPlayer() {
    miniPlayer = this.activity.get().findViewById(R.id.ll_mini_player);
    rlControlPlayback = miniPlayer.findViewById(R.id.rl_mini_player_control_playback);
    ivControlPlayback = miniPlayer.findViewById(R.id.iv_mini_player_control_playback);
    rlControlStop = miniPlayer.findViewById(R.id.rl_mini_player_control_stop);
    rlControlFastForward = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_forward);
    rlControlFastRewind = miniPlayer.findViewById(R.id.rl_mini_player_control_fast_rewind);
    tvTitle = miniPlayer.findViewById(R.id.tv_mini_player_channel_title);
    tvSubitle = miniPlayer.findViewById(R.id.tv_mini_player_track_title);

    // set Click listeners
    if(rlControlPlayback != null)
      rlControlPlayback.setOnClickListener(this);
    if(rlControlStop != null)
      rlControlStop.setOnClickListener(this);
    if(rlControlFastForward != null)
      rlControlFastForward.setOnClickListener(this);
    if(rlControlFastRewind != null)
      rlControlFastRewind.setOnClickListener(this);
  }

  public void setEpisode(Episode episode, int state) {
    this.episode = episode;
    ivControlPlayback.setImageResource(PlaybackButtonHelper.getPlayerPlaybackButtonResId(state));
    miniPlayer.setTag(state);
    tvTitle.setText(episode.getTitle());
    /*mTimeLeft.setText(AdapterHelper.buildDurationString(
        mActivity.get(),
        episode.getEpisodeStatus(),
        episode.getDuration(),
        episode.getProgress()));*/

    // load image into the channel art view
    /*ImageLoadHelper.loadImageIntoView(mMiniPlayer.getContext(), episode.getChannelArtworkUrl(),
        mChannelArt);*/

    // load image so that we can extract colors from it
    /*ImageLoadHelper.loadImageAsync(mMiniPlayer.getContext(), mEpisode.getChannelArtworkUrl(),
        new ImageLoadedListener(this));*/
    showPlayer();
  }

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

  public void clearMetadata(){
    if(tvTitle != null){
      tvTitle.setText(null);
    }
    if(tvSubitle != null){
      tvSubitle.setText(null);
    }
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

  // TODO: add logo and favorites

  @Override public void onClick(View v) {
    switch (v.getId()){
      case R.id.rl_mini_player_control_playback:
        Log.d(TAG, "Mini player: playback");
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

  public interface ControlListener{
    boolean onShow();
    boolean onHide();
  }
}
