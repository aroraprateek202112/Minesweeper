package com.example.prateek.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Prateek on 02-12-2015.
 */

// Easy - 9x9 grid - 10 mines
// Medium - 16x16 grid - 40 mines
// Hard - 30X16 grid - 99 mines
public class Game extends Activity {

  private static final String LOG_TAG = Game.class.getSimpleName();

  // Views
  private Tile[][] mTitles;
  private TableLayout mMineField;
  private ImageView mImageButton;
  private TextView mTimerText;

  private boolean timerStarted;
  private boolean minesSet;

  // No. of rows, coloumns and mines.
  // This will vary based on difficulty level
  int totalRows;
  int totalCols;
  int totalMines;

  // margin b/w tiles
  private int tileMargin;// = 1;

  private Handler timer;
  private int secondPassed = 0;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.game);
    mMineField = (TableLayout) findViewById(R.id.MineField);
    mImageButton = (ImageView) findViewById(R.id.Smiley);
    mTimerText = (TextView) findViewById(R.id.Timer);

    int difficulty = getIntent().getIntExtra(IConstants.KEY_DIFFICULTY, IConstants.DIFFICULTY_EASY);
    initValues();
    createGameBoard(difficulty);
    showGameBoard();
  }

  private void initValues() {

    totalRows = IConstants.NO_OF_ROWS_FOR_DIFFICULTY_EASY;
    totalCols = IConstants.NO_OF_COLOUMNS_FOR_DIFFICULTY_EASY;
    totalMines = IConstants.NO_OF_MINES_FOR_DIFFICULTY_EASY;

    tileMargin = getResources().getDimensionPixelSize(R.dimen.margin_btw_tiles);
  }

  private void createGameBoard(int difficulty) {

    //set total rows and columns based on the difficulty

    switch (difficulty) {
      case IConstants.DIFFICULTY_EASY:
        break;
      case IConstants.DIFFICULTY_Medium:
        totalRows = IConstants.NO_OF_ROWS_FOR_DIFFICULTY_MEDIUM;
        totalCols = IConstants.NO_OF_COLOUMNS_FOR_DIFFICULTY_MEDIUM;
        totalMines = IConstants.NO_OF_MINES_FOR_DIFFICULTY_MEDIUM;
        break;
      case IConstants.DIFFICULTY_HARD:
        totalRows = IConstants.NO_OF_ROWS_FOR_DIFFICULTY_HARD;
        totalCols = IConstants.NO_OF_COLOUMNS_FOR_DIFFICULTY_HARD;
        totalMines = IConstants.NO_OF_MINES_FOR_DIFFICULTY_HARD;
        break;
    }

    // setup the tiles array
    mTitles = new Tile[totalRows][totalCols];

    for (int row = 0; row < totalRows; row++) {

      for (int col = 0; col < totalCols; col++) {
        //create a tile
        mTitles[row][col] = new Tile(this);

        //set the tile defaults
        mTitles[row][col].setDefaults();

        final int curRow = row;
        final int curCol = col;

        //add a click listener
        mTitles[row][col].setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

            if (!timerStarted) {
              timerStarted = true;
            }

            if (!minesSet) {
              setUpMineField(curRow, curCol);
              minesSet = true;
            }

            if (!mTitles[curRow][curCol].isFlag()) {
              if (mTitles[curRow][curCol].isMine()) {
                loseGame();
              } else if (wonGame()) {
                winGame();
              } else {
                uncoverTiles(curRow, curCol);
              }
            }
          }
        });

        //add a long click listener
        mTitles[row][col].setOnLongClickListener(new View.OnLongClickListener() {
          @Override
          public boolean onLongClick(View v) {
            return true;
          }
        });
      }
    }
  }

  private void showGameBoard() {
    if (totalRows <= 0) {
      return;
    }

    Log.d(LOG_TAG, "showGameBoard TableRow width ["
        + getResources().getDisplayMetrics().widthPixels
        + "] height ["
        + getResources().getDisplayMetrics().heightPixels
        + "]");

    // tileWidth
    // = ((screen width - left and right padding) / no. of columns) - left and right margin b/w tile
    int tileWidth = ((getResources().getDisplayMetrics().widthPixels - (2
        * getResources().getDimensionPixelSize(R.dimen.margin_btw_tiles_and_border))) / totalCols)
        - (2 * tileMargin);
    int tileHeight =
        getResources().getDimensionPixelSize(R.dimen.minimum_tile_height) < tileWidth ? tileWidth
            : getResources().getDimensionPixelSize(R.dimen.minimum_tile_height);

    TableRow.LayoutParams tileParams = new TableRow.LayoutParams(tileWidth, tileHeight);
    tileParams.setMargins(tileMargin, tileMargin, tileMargin, tileMargin);

    Log.d(LOG_TAG, "showGameBoard Tile width [" + tileWidth + "] height [" + tileHeight + "]");
    Log.d(LOG_TAG, "showGameBoard tileMargin in pixels [" + tileMargin + "]");

    TableRow tileRow = null;

    TableRow.LayoutParams tileRowParams =
        new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tileHeight + 2 * tileMargin);

    // for every row
    for (int row = 0; row < totalRows; row++) {

      //create a new table row
      tileRow = new TableRow(this);

      //for every column
      for (int col = 0; col < totalCols; col++) {

        //set the width and height of the tile
        mTitles[row][col].setLayoutParams(tileParams);

        //add the tile to the table row
        tileRow.addView(mTitles[row][col]);
      }
      //set the height and width of the row
      tileRow.setLayoutParams(tileRowParams);
      mMineField.addView(tileRow, tileRowParams);
      //Log.d(LOG_TAG, "showGameBoard addView TableRow width ["+((tileWH +2 * tileMargin) * totalCols)+"] height ["+ (tileWH + 2 * tileMargin)+"]");
    }
  }

  //private void showGameBoard() {
  //
  //  // for every row
  //  for (int row = 0; row < totalRows; row++) {
  //
  //    //create a new table row
  //    TableRow tableRow = new TableRow(this);
  //
  //    //set the height and width of the row
  //    tableRow.setLayoutParams(
  //        new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tileWH + 2 * tileMargin));
  //    mMineField.addView(tableRow,
  //        new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, tileWH + 2 * tileMargin));
  //    //Log.d(LOG_TAG, "showGameBoard TableRow width ["+mMineField.getLayoutParams().width+"] height ["+ mMineField.getLayoutParams().height+"]");
  //    Log.d(LOG_TAG, "showGameBoard TableRow width ["+getResources().getDisplayMetrics().widthPixels+"] height ["+ mMineField.getLayoutParams().height+"]");
  //
  //    //for every column
  //    int tileWidth = (getResources().getDisplayMetrics().widthPixels / totalCols) - (2 * Util.dpToPixels(getResources().getDisplayMetrics(), 5));
  //    TableRow.LayoutParams params = null;
  //    for (int col = 0; col < totalCols; col++) {
  //
  //      params = new TableRow.LayoutParams(tileWidth, tileWH);
  //      params.setMargins(tileMargin, tileMargin, tileMargin, tileMargin);
  //      //set the width and height of the tile
  //      mTitles[row][col].setLayoutParams(params);
  //      Log.d(LOG_TAG, "showGameBoard Tile width ["+tileWidth+"] height ["+ tileWH+"]");
  //
  //      //add some padding to the tile
  //      //mTitles[row][col].setPadding(tileMargin, tileMargin, tileMargin, tileMargin);
  //
  //      //add the tile to the table row
  //      tableRow.addView(mTitles[row][col]);
  //    }
  //    //Log.d(LOG_TAG, "showGameBoard addView TableRow width ["+((tileWH +2 * tileMargin) * totalCols)+"] height ["+ (tileWH + 2 * tileMargin)+"]");
  //  }
  //}

  private void setUpMineField(int row, int col) {

    //Log.d(LOG_TAG, "setUpMineField");

    Random random = new Random();
    int mineRow, mineCol;
    for (int i = 0; i < totalMines; i++) {

      mineRow = random.nextInt(totalRows);//+1;
      mineCol = random.nextInt(totalCols);//+1;

      if (mineRow == row && mineCol == col) {
        i--;
      } else if (mTitles[mineRow][mineCol].isMine()) {
        i--;
      } else {
        //plant a new mine
        mTitles[mineRow][mineCol].plantMine();

        Log.d(LOG_TAG,
            "setUpMineField [" + i + "] at mineRow [" + mineRow + "] mineCol [" + mineCol + "]");
        //go one row and col back
        int startRow = mineRow - 1;
        int startCol = mineCol - 1;

        //check 3 rows across and 3 down
        int checkRows = 3;
        int checkCols = 3;

        if (startRow < 0) //if it is on the first row
        {
          startRow = 0;
          checkRows = 2;
        } else if (startRow + 3 > totalRows) {
          checkRows = 2;
        }

        if (startCol < 0) {
          startCol = 0;
          checkCols = 2;
        } else if (startCol + 3 > totalCols) {
          checkCols = 2;
        }

        for (int j = startRow; j < startRow + checkRows; j++) {
          for (int k = startCol; k < startCol + checkCols; k++) {
            if (!mTitles[j][k].isMine()) {
              mTitles[j][k].updateSurroundingMineCount();
              Log.d(LOG_TAG,
                  "setUpMineField [" + i + "] mine count at j [" + j + "] k [" + k + "]");
            }
          }
        }
      }
    }
  }

  private void uncoverTiles(int row, int col) {

    // if the tile is mine or flag, then return
    if (mTitles[row][col].isMine() || mTitles[row][col].isFlag()) {
      return;
    }

    mTitles[row][col].openTile();

    if (mTitles[row][col].getNoSurroundingMines() > 0) {
      return;
    }

    // go one row and column back
    int startRow = row - 1;
    int startCol = col - 1;

    // check 3 rows across and 3 down
    int checkRows = 3;
    int checkCols = 3;

    if (startRow < 0) {
      startRow = 0;
      checkRows = 2;
    } else if (startRow + 3 > totalRows) {
      checkRows = 2;
    }

    if (startCol < 0) {
      startCol = 0;
      checkCols = 2;
    } else if (startCol + 3 > totalCols) {
      checkCols = 2;
    }

    for (int i = startRow; i < startRow + checkRows; i++) {
      for (int j = startCol; j < startCol + checkCols; j++) {

        if (mTitles[i][j].isCovered()) {
          uncoverTiles(i, j);
        }
      }
    }
  }

  private void winGame() {
  }

  private boolean wonGame() {
    return false;
  }

  private void loseGame() {

    stopTimer();
    mImageButton.setBackgroundResource(R.drawable.lose);

    // Uncover all the tiles
    for (int i = 0; i < totalRows; i++) {
      for (int j = 0; j < totalCols; j++) {

        // If tile is covered
        if (mTitles[i][j].isCovered()) {

          // if there is no flag or no mine
          if (!mTitles[i][j].isFlag() && !mTitles[i][j].isMine()) {
            mTitles[i][j].openTile();
            // if  there is mine but no flag
          } else if (mTitles[i][j].isMine() && !mTitles[i][j].isFlag()) {
            mTitles[i][j].openTile();
          }
        }
      }
    }
  }

  public void endGame() {
    mImageButton.setBackgroundResource(R.drawable.smile);

    // remove the table rows from the minefield table layout
    mMineField.removeAllViews();

    // reset variables
    timerStarted = false;
    minesSet = false;
  }

  public void startTimer() {

    if (secondPassed == 0) {
      timer.removeCallbacks(updateTimer);
      timer.postDelayed(updateTimer, 1000);
    }
  }

  public void stopTimer() {
    if (timer != null) timer.removeCallbacks(updateTimer);
  }

  private Runnable updateTimer = new Runnable() {
    @Override
    public void run() {
      long currentMilliseconds = System.currentTimeMillis();
      ++secondPassed;

      String curTime = Integer.toString(secondPassed);

      // update the text view
      if (secondPassed < 10) {
        mTimerText.setText("00" + curTime);
      } else if (secondPassed < 100) {
        mTimerText.setText("0" + curTime);
      } else {
        mTimerText.setText(curTime);
      }
      timer.postAtTime(this, currentMilliseconds);

      // run again in 1 second
      timer.postDelayed(updateTimer, 1000);
    }
  };
}
