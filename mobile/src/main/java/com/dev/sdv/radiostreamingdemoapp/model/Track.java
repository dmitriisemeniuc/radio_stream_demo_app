package com.dev.sdv.radiostreamingdemoapp.model;

public abstract class Track {

  protected String title;
  protected String url;

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }
}
