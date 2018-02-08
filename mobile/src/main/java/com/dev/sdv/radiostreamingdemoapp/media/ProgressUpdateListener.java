package com.dev.sdv.radiostreamingdemoapp.media;

public interface ProgressUpdateListener {

  void onProgressUpdate(long progress, long bufferedProgress, long duration);
}
