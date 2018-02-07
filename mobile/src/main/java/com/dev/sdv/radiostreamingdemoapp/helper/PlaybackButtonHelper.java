package com.dev.sdv.radiostreamingdemoapp.helper;

import android.media.session.PlaybackState;
import com.dev.sdv.radiostreamingdemoapp.R;

public class PlaybackButtonHelper {

  public static int getPlayerPlaybackButtonResId(int state){
    // set up the appropriate button configuration
    switch (state) {
      /*case PlaybackState.STATE_CONNECTING:
      case PlaybackState.STATE_BUFFERING:
        return R.drawable.ic_action_buffering;*/
      case PlaybackState.STATE_PLAYING:
        return R.drawable.ic_pause_black_24px;
      case PlaybackState.STATE_NONE:
      case PlaybackState.STATE_PAUSED:
      case PlaybackState.STATE_STOPPED:
        return R.drawable.ic_play_arrow_black_24px;
      default:
        return -1;
    }
  }
}
