package com.dev.sdv.radiostreamingdemoapp.model;

import android.text.TextUtils;
import android.util.Log;
import java.util.LinkedList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Playlist {

  private static final String TAG = Playlist.class.getSimpleName();
  private static final String TRACK_IDS = "trackIds";
  private static final String CURRENT_INDEX = "currentIndex";

  private final LinkedList<String> trackServerIds;
  private int currentIndex = -1;

  public Playlist(int currentIndex, LinkedList<String> trackServerIds) {
    this.currentIndex = currentIndex;
    this.trackServerIds = trackServerIds;
  }

  public Playlist() {
    trackServerIds = new LinkedList<>();
  }

  public Playlist(List<String> trackServerIds) {
    this.trackServerIds = new LinkedList<>(trackServerIds);

    if (trackServerIds.size() > 0) {
      currentIndex = 0;
    }
  }

  public Playlist(String jsonStr) {
    trackServerIds = new LinkedList<>();

    if (TextUtils.isEmpty(jsonStr)) {
      return;
    }

    try {
      JSONObject json = new JSONObject(jsonStr);
      currentIndex = json.getInt(CURRENT_INDEX);
      JSONArray jsonArray = json.getJSONArray(TRACK_IDS);

      for (int i = 0; i < jsonArray.length(); i++) {
        this.trackServerIds.add(jsonArray.getString(i));
      }
    } catch (JSONException e) {
      Log.w(TAG, e);
    }
  }

  public void addToEnd(String trackServerId) {
    trackServerIds.addLast(trackServerId);

    if (currentIndex == -1) {
      currentIndex = 0;
    }
  }

  public void addToBeginning(String episodeServerId) {
    trackServerIds.addFirst(episodeServerId);

    if (currentIndex == -1) {
      currentIndex = 0;
    }
  }

  public String getCurrentTrackServerId() {

    if (currentIndex != -1  && currentIndex + 1  <= trackServerIds.size()) {
      return trackServerIds.get(currentIndex);
    }
    return null;
  }

  public boolean previous() {

    if (currentIndex - 1 >= 0) {
      currentIndex--;
      return true;
    }
    return false;
  }

  public boolean next() {

    if (currentIndex + 1 < trackServerIds.size()) {
      currentIndex++;
      return true;
    }
    return false;
  }

  public boolean moveToTrack(String trackId) {

    for (int i = 0; i < trackServerIds.size(); i++) {

      if (trackServerIds.get(i).contentEquals(trackId)) {
        currentIndex = i;
        return true;
      }
    }
    return false;
  }

  public List<String> getTrackServerIds() {
    return trackServerIds;
  }

  public int getCurrentIndex() {
    return currentIndex;
  }

  public String toJsonString() {
    JSONObject json = new JSONObject();
    JSONArray episodeIdArr = new JSONArray();

    try {
      for (int i = 0; i < trackServerIds.size(); i++) {
        episodeIdArr.put(i, trackServerIds.get(i));
      }
      json.put(TRACK_IDS, episodeIdArr);
      json.put(CURRENT_INDEX, currentIndex);
    } catch (JSONException e) {
      Log.w(TAG, e);
    }
    return json.toString();
  }

  @Override
  public String toString() {
    return toJsonString();
  }
}
