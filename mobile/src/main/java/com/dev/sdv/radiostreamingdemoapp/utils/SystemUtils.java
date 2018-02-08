package com.dev.sdv.radiostreamingdemoapp.utils;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.provider.MediaStore;
import com.dev.sdv.radiostreamingdemoapp.media.service.MediaService;

public class SystemUtils {

  /**
   * Returns true if {@link Service} is running
   *
   * Used for {@link MediaService} class
   * */
  public static boolean isServiceRunning(Class<?> serviceClass, Context context) {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (serviceClass.getName().equals(service.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}
