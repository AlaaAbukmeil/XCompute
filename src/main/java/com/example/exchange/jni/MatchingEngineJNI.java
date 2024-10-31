/* (C)2024 */
package com.example.exchange.jni;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MatchingEngineJNI {
  private final String libraryPath;

  public MatchingEngineJNI(@Value("${native.library.path}") String libraryPath) {
    this.libraryPath = libraryPath;
    loadLibrary();
  }

  private void loadLibrary() {
    try {
      File libFile = new File(libraryPath, "libmatching_engine.dylib");
      if (!libFile.exists()) {
        throw new RuntimeException("Library file not found: " + libFile.getAbsolutePath());
      }
      System.load(libFile.getAbsolutePath());
    } catch (Exception e) {
      throw new RuntimeException("Failed to load native library", e);
    }
  }

  public native String printHello();
}
