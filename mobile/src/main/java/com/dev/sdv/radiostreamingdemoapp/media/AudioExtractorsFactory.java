package com.dev.sdv.radiostreamingdemoapp.media;

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp3.Mp3Extractor;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;
import com.google.android.exoplayer2.extractor.ogg.OggExtractor;
import com.google.android.exoplayer2.extractor.wav.WavExtractor;

public class AudioExtractorsFactory implements ExtractorsFactory {

  @Override public Extractor[] createExtractors() {
    return new Extractor[]{
        new OggExtractor(),
        new WavExtractor(),
        new Mp3Extractor(),
        new Mp4Extractor()};
  }
}
