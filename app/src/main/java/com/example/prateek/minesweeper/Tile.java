package com.example.prateek.minesweeper;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

/**
 * Created by prateek02.arora on 02-12-2015.
 */

// isMine - is the tile a mine (uncovered)
// isFlag-is the tile a flag(covered/marked)
// isQuestionMark-is the tile a question mark(covered/marked)
// isCovered-is the tile covered(covered)
// noSurroundingMines-the number of surrounding mines

public class Tile extends android.support.v7.widget.AppCompatButton {

  private static final String LOG_TAG = Tile.class.getSimpleName();
  private boolean isMine;
  private boolean isFlag;
  private boolean isQuestionMark;
  private boolean isCovered;
  private int noSurroundingMines;

  public Tile(Context context) {
    super(context);
  }

  public Tile(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  public Tile(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
  }

  public boolean isMine() {
    return isMine;
  }

  public void setMine(boolean mine) {
    isMine = mine;
  }

  public boolean isFlag() {
    return isFlag;
  }

  public void setFlag(boolean flag) {
    isFlag = flag;
  }

  public boolean isQuestionMark() {
    return isQuestionMark;
  }

  public void setQuestionMark(boolean questionMark) {
    isQuestionMark = questionMark;
  }

  public boolean isCovered() {
    return isCovered;
  }

  public void setCovered(boolean covered) {
    isCovered = covered;
  }

  public int getNoSurroundingMines() {
    return noSurroundingMines;
  }

  public void setNoSurroundingMines(int noSurroundingMines) {
    this.noSurroundingMines = noSurroundingMines;
  }

  public void setDefaults() {
    isMine = false;
    isFlag = false;
    isQuestionMark = false;
    isCovered = true;
    noSurroundingMines = 0;

    this.setBackgroundColor(Color.parseColor("#7D7B7B"));
    //        this.setBackgroundResource(R.drawable.tile);
  }

  public void setUncovered() {
    isCovered = false;
  }

  public void plantMine() {
    isMine = true;
    //        this.setBackgroundResource(R.drawable.mine);
  }

  // Show the mineIcon
  public void triggerMine() {
    this.setBackgroundResource(R.drawable.mine);
  }

  // Shows number icon
  public void showNumbers() {
    String img = "mines" + noSurroundingMines;
    //Log.d(LOG_TAG, "showNumbers img :"+img);
    int drawableId =
        getResources().getIdentifier(img, "drawable", "com.example.prateek.minesweeper");
    //Log.d(LOG_TAG, "showNumbers drawableId :"+drawableId);
    if (drawableId != 0) {
      this.setBackgroundResource(drawableId);
    } else {
      this.setBackgroundColor(Color.parseColor("#DAD2D2"));
    }
  }

  // Uncover the tile
  public void openTile() {

    if (!isCovered) {
      return;
    }

    setUncovered();
    if (isMine) {
      triggerMine();
    } else {
      //showNumbers();
      this.setBackgroundResource(R.drawable.circular_bg_drawable);
    }
  }

  public void updateSurroundingMineCount() {
    noSurroundingMines++;
    //Log.d(LOG_TAG, "updateSurroundingMineCount noSurroundingMines [" + noSurroundingMines + "]");
    //String img = "mines"+noSurroundingMines;
    //Log.d(LOG_TAG, "updateSurroundingMineCount img [" + img + "]");
    //int drawableId = getResources().getIdentifier(img, "drawable", "com.example.prateek.minesweeper");
    //Log.d(LOG_TAG, "updateSurroundingMineCount drawableId [" + drawableId + "]");
    //this.setBackgroundResource(drawableId);
  }
}
