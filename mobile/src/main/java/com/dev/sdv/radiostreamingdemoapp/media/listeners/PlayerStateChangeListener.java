package com.dev.sdv.radiostreamingdemoapp.media.listeners;

import android.content.Context;
import android.content.Intent;

public interface PlayerStateChangeListener {

  void onReceive(Context context, Intent intent);
}
