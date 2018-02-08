package com.dev.sdv.radiostreamingdemoapp.media.listeners;

public interface ProgressUpdateListener {

  void onProgressUpdate(long progress, long bufferedProgress, long duration);
}
