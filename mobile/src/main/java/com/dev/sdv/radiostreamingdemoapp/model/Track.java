package com.dev.sdv.radiostreamingdemoapp.model;

public abstract class Track {

  protected int        id;
  protected String     title;
  protected String     url;

  public abstract void setStatus(int status);

  public abstract int getStatus();

  public abstract void setProgress(long progress);

  public abstract long getProgress();

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

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
