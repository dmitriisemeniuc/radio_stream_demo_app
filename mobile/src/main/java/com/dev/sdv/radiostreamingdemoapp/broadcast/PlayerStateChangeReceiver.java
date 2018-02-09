package com.dev.sdv.radiostreamingdemoapp.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.dev.sdv.radiostreamingdemoapp.media.listeners.PlayerStateChangeListener;
import java.lang.ref.WeakReference;

public class PlayerStateChangeReceiver extends BroadcastReceiver {

  private final WeakReference<PlayerStateChangeListener> listener;

  public PlayerStateChangeReceiver(PlayerStateChangeListener listener){
    this.listener = new WeakReference<>(listener);
  }

  @Override public void onReceive(Context context, Intent intent) {
    PlayerStateChangeListener listener = this.listener.get();

    if(listener == null){
      return;
    }
    listener.onReceive(context, intent);
  }
}
