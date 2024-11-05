/* (C)2024 */
package com.example.exchange.jni;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PreDestroy;

@Component
public class MatchingEngineJNI implements AutoCloseable {
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

  public native long createMatchingEngine(String symbol);

  public native String insertOrder(long handle, String id, String type, int price, int amount);

  public native String getMatchingEngineSummary(long handle);

  public native String printHello();

  private long nativePtr;

  public native void deleteMatchingEngine(long ptr);

  @PreDestroy
  @Override
  public void close() {
    if (nativePtr != 0) {
      deleteMatchingEngine(nativePtr);
      nativePtr = 0;
    }
  }
}
