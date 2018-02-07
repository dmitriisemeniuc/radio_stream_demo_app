package com.dev.sdv.radiostreamingdemoapp.media.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class MediaService extends Service {

  public static final String TAG = MediaService.class.getSimpleName();

  private IBinder mediaServiceBinder = new MediaServiceBinder();

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "Media service started");
    super.onStartCommand(intent, flags, startId);
    return START_STICKY;
  }

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "in onCreate");
  }

  @Nullable @Override public IBinder onBind(Intent intent) {
    return mediaServiceBinder;
  }

  @Override
  public void onRebind(Intent intent) {
    Log.v(TAG, "in onRebind");
    super.onRebind(intent);
  }

  @Override
  public boolean onUnbind(Intent intent) {
    Log.v(TAG, "in onUnbind");
    return true;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "Media service destroyed");
  }

  /**
   * Binder of Gps Location Service. Need us to send activity listener to Gps Location Service.
   */
  public class MediaServiceBinder extends Binder {

    public MediaService getService() {
      return MediaService.this;
    }

    /*public void setListener(Object listener) {
      this.listener = listener;
    }*/
  }
}
