package com.dev.sdv.radiostreamingdemoapp.model;

import android.content.Context;

public class TrackModel {

  private static final String TAG = TrackModel.class.getSimpleName();

  public static final String PARAM_TRACK_STATUS       = "trackStatus";
  public static final String PARAM_TRACK_PROGRESS     = "trackProgress";
  public static final String PARAM_TRACK_DURATION     = "trackDuration";
  public static final String PARAM_DOWNLOAD_STATUS    = "downloadStatus";
  public static final String PARAM_MANUAL_DOWNLOAD    = "manualDownload";
  public static final String PARAM_TOGGLE_FAVORITE    = "toggleFavorite";
  public static final String PARAM_SIZE               = "size";
  public static final String PARAM_MEDIA_URL          = "localMediaUrl";
  public static final String PARAM_DOWNLOADED_SIZE    = "downloadedSize";
  public static final String PARAM_MANUALLY_ADDED     = "manuallyAdded";

  public static final int UPDATE = 0;
  public static final int ADD    = 1;

  public static Track getTrackById(Context context, String trackId) {
    return PodcastEpisodeModel.getDefault();
  }
}
