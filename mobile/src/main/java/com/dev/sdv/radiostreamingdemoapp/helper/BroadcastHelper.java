package com.dev.sdv.radiostreamingdemoapp.helper;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

public class BroadcastHelper {

  public static final String INTENT_PLAYER_STATE_CHANGE = "com.dev.sdv.radiostreamingdemoapp.playerStateChange";
  public static final String INTENT_PROGRESS_UPDATE     = "com.dev.sdv.radiostreamingdemoapp.progressUpdate";
  public static final String INTENT_TRACK_PROCESSED     = "com.dev.sdv.radiostreamingdemoapp.podcastProcessed";

  public static final String EXTRA_TRACK_ID          = "trackId";
  public static final String EXTRA_PLAYER_STATE      = "playerStateId";
  public static final String EXTRA_PROGRESS          = "progress";
  public static final String EXTRA_BUFFERED_PROGRESS = "bufferedProgress";
  public static final String EXTRA_DURATION          = "duration";
  public static final String EXTRA_CHANNEL           = "channel";
  public static final String EXTRA_SUCCESS           = "success";

  public static void broadcastPlayerStateChange(Context context, int playerStateId,
      String trackId) {
    Intent intent = new Intent(INTENT_PLAYER_STATE_CHANGE);
    intent.putExtra(EXTRA_TRACK_ID, trackId);
    intent.putExtra(EXTRA_PLAYER_STATE, playerStateId);
    sendBroadcast(context, intent);
  }

  public static void broadcastProgressUpdate(Context context, long progress, long bufferedProgress,
      long duration) {
    Intent intent = new Intent(INTENT_PROGRESS_UPDATE);
    intent.putExtra(EXTRA_PROGRESS, progress);
    intent.putExtra(EXTRA_BUFFERED_PROGRESS, bufferedProgress);
    intent.putExtra(EXTRA_DURATION, duration);
    sendBroadcast(context, intent);
  }

  private static void sendBroadcast(Context context, Intent intent) {
    if(intent != null){
      if(!INTENT_PROGRESS_UPDATE.contentEquals(intent.getAction())){
        Logger.d("Broadcasting intent", intent.getAction());
      }
      LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
  }
}
