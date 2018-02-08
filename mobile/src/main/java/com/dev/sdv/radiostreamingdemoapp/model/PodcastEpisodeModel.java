package com.dev.sdv.radiostreamingdemoapp.model;

import com.dev.sdv.radiostreamingdemoapp.utils.Const;

public class PodcastEpisodeModel {

  public static PodcastEpisode getDefault(){
    return new PodcastEpisode.Builder()
        .id(1)
        .title(Const.PodcastEpisode.DEFAULT_PODCAST_TITLE)
        .subtitle(Const.PodcastEpisode.DEFAULT_PODCAST_SUBTITLE)
        .url(Const.PodcastEpisode.DEFAULT_PODCAST_URL)
        .build();
  }
}
