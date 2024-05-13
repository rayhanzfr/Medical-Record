package com.prodia.technical.common.helper;

import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileNameUtil {

  public static String getExtension(@NotNull String filename) {
    if(filename == null || filename.isBlank()) return "";
    return filename.substring(filename.lastIndexOf(".") + 1);
  }
  
  public static boolean isMatchExtension(@NotNull String filename, @NotNull String regexExtension) {
    if(filename == null || filename.isBlank()) return false;
    return filename.substring(filename.lastIndexOf(".") + 1).toUpperCase().matches(regexExtension.toUpperCase());
  }

  public static String getPath(String fullPath) {
    if(fullPath == null || fullPath.isBlank()) return "";
    return fullPath.substring(0, fullPath.lastIndexOf("/"));
  }

  public static String getName(@NotNull String filename) {
    if(filename == null || filename.isBlank()) return "";
    return filename.substring(0, filename.lastIndexOf("."));
  }
  
}