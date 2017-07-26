package com.example.prateek.minesweeper;

import android.util.DisplayMetrics;
import android.util.TypedValue;

/**
 * Created on 26/7/17.
 */

public class Util {

  public static int dpToPixels(DisplayMetrics metrics, int dp) {
    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
  }
}
