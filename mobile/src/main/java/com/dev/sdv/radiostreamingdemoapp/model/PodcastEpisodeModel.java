package com.dev.sdv.radiostreamingdemoapp.model;

import com.dev.sdv.radiostreamingdemoapp.utils.Const;

public class PodcastEpisodeModel {

  public static PodcastEpisode getDefault(){
    return new PodcastEpisode.Builder()
        .id(1)
        .title(Const.Track.DEBUG_PODCAST_TITLE)
        .subtitle(Const.Track.DEBUG_PODCAST_SUBTITLE)
        .url(Const.Track.DEBUG_PODCAST_URL)
        .build();
  }
}
