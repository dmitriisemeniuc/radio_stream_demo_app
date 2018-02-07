package com.dev.sdv.radiostreamingdemoapp.model;

public class PodcastEpisode extends Track {

  private int id = -1;
  private String subtitle;

  public PodcastEpisode(){

  }

  public PodcastEpisode(EpisodeBuilder builder) {
    this.id = builder.id;
    this.title = builder.title;
    this.subtitle = builder.subtitle;
    this.url = builder.url;
  }

  /**
   * Copy PodcastEpisode
   * */
  public PodcastEpisode(PodcastEpisode episode) {
    title = episode.getTitle();
    subtitle = episode.getSubtitle();
    url = episode.getUrl();
  }

  public int getId() {
    return id;
  }

  public String getSubtitle() {
    return subtitle;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PodcastEpisode episode = (PodcastEpisode) o;

    if (id != episode.id) return false;
    if (title != null ? !title.equals(episode.title) : episode.title != null) return false;
    if (subtitle != null ? !subtitle.equals(episode.subtitle) : episode.subtitle != null) {
      return false;
    }
    return url != null ? url.equals(episode.url) : episode.url == null;
  }

  @Override public int hashCode() {
    int result = id;
    result = 31 * result + (title != null ? title.hashCode() : 0);
    result = 31 * result + (subtitle != null ? subtitle.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "PodcastEpisode{" +
        "id=" + id +
        ", title='" + title + '\'' +
        ", subtitle='" + subtitle + '\'' +
        ", url='" + url + '\'' +
        '}';
  }

  public static class EpisodeBuilder {
    private int id = -1;
    private String title;
    private String subtitle;
    private String url;

    public EpisodeBuilder id(int id) {
      this.id = id;
      return this;
    }

    public EpisodeBuilder title(String title) {
      this.title = title;
      return this;
    }

    public EpisodeBuilder subtitle(String subtitle) {
      this.subtitle = subtitle;
      return this;
    }

    public EpisodeBuilder url(String url) {
      this.url = url;
      return this;
    }

    public PodcastEpisode build() {
      return new PodcastEpisode(this);
    }
  }
}
