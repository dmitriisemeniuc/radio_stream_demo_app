package com.dev.sdv.radiostreamingdemoapp.media;

import android.media.session.MediaController;
import android.media.session.PlaybackState;
import android.os.Bundle;
import android.support.annotation.NonNull;
import com.dev.sdv.radiostreamingdemoapp.ui.activity.BaseActivity;
import java.lang.ref.WeakReference;

public class MediaControllerCallback extends MediaController.Callback {

  private WeakReference<BaseActivity> mActivity;

  public MediaControllerCallback(BaseActivity activity) {
    mActivity = new WeakReference<>(activity);
  }

  @Override
  public void onExtrasChanged(Bundle extras) {
    onStateChanged();
  }

  @Override
  public void onPlaybackStateChanged(@NonNull PlaybackState state) {
    onStateChanged();
  }

  private void onStateChanged() {
    BaseActivity activity = mActivity.get();

    if (activity == null) {
      return;
    }

    activity.onStateChanged(activity.getState(), activity.getTrack());
  }
}
