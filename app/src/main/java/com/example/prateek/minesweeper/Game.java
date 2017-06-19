package com.example.prateek.minesweeper;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
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
public class Game extends Activity{

    private static final String LOG_TAG = Game.class.getSimpleName();

    public static final String KEY_DIFFICULTY ="difficulty";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_Medium = 1;
    public static final int DIFFICULTY_HARD = 2;
    private Tile[][] mTitles;
    private TableLayout mMineField;

    private boolean timerStarted;
    private boolean minesSet;

    int totalRows = 9;
    int totalCols = 9;
    int totalMines = 10;
    private ImageView mImageButton;
    private TextView mTimerText;

    private Handler timer;
    private int secondPassed = 0;
    private int tileWH = 40;
    private int tilePadding = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.game);
        mMineField = (TableLayout)findViewById(R.id.MineField);
        mImageButton = (ImageView) findViewById(R.id.Smiley);
        mTimerText = (TextView)findViewById(R.id.Timer);

        int difficulty = getIntent().getIntExtra(KEY_DIFFICULTY, DIFFICULTY_EASY);
        createGameBoard(difficulty);
        showGameBoard();

    }

    private void createGameBoard(int difficulty) {

        //set total rows and columns based on the difficulty

        switch (difficulty) {
            case 0:
                break;
            case 1:
                totalRows = 16;
                totalCols = 16;
                totalMines = 40;
                break;
            case 2:
                totalRows = 30;
                totalCols = 16;
                totalMines = 99;
                break;
        }

        // setup the tiles array
        mTitles = new Tile[totalRows][totalCols];

        for(int row = 0; row < totalRows; row++) {

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
                                //uncoverTiles(curRow, curCol);
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

        // for every row
        for(int row = 0; row < totalRows; row++) {

            //create a new table row
            TableRow tableRow = new TableRow(this);

            //set the height and width of the row
            tableRow.setLayoutParams(new TableRow.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));

            //for every column
            for(int col=0; col<totalCols; col++) {

                //set the width and height of the tile
                mTitles[row][col].setLayoutParams(new TableRow.LayoutParams(tileWH * tilePadding,  tileWH * tilePadding));

                //add some padding to the tile
                mTitles[row][col].setPadding(tilePadding, tilePadding, tilePadding, tilePadding);

                //add the tile to the table row
                tableRow.addView(mTitles[row][col]);

            }
            mMineField.addView(tableRow, new TableLayout.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));
        }
    }

    private void setUpMineField(int row, int col) {

        Log.d(LOG_TAG, "setUpMineField");

        Random random = new Random();
        int mineRow, mineCol;
        for (int i = 0; i < totalMines; i++) {

            mineRow = random.nextInt(totalRows);//+1;
            mineCol = random.nextInt(totalCols);//+1;

            Log.d(LOG_TAG, "setUpMineField ["+i+"] mineRow ["+mineRow+"]");
            Log.d(LOG_TAG, "setUpMineField ["+i+"] mineCol ["+mineCol+"]");

            if (mineRow == row+1 && mineCol == col+1) {
                Log.d(LOG_TAG, "setUpMineField if block");
                i--;
            } else if (mTitles[mineRow][mineCol].isMine()) {
                Log.d(LOG_TAG, "setUpMineField else if block");
                i--;
            } else {
                //plant a new mine
                mTitles[mineRow][mineCol].plantMine();

                Log.d(LOG_TAG, "setUpMineField else block plant mine");
                //go one row and col back
                int startRow = mineRow - 1;
                int startCol = mineCol - 1;

                Log.d(LOG_TAG, "setUpMineField else block plant mine startRow ["+startRow+"] startCol ["+startCol+"]");

                //check 3 rows across and 3 down
                int checkRows = 3;
                int checkCols = 3;

                if(startRow < 0) //if it is on the first row
                {
                    startRow = 0;
                    checkRows = 2;
                } else if (startRow + 3 > totalRows) {
                    checkRows = 2;
                }

                if(startCol < 0)
                {
                    startCol = 0;
                    checkCols = 2;
                }
                else if(startCol+3 > totalCols) {
                    checkCols = 2;
                }

                for (int j = 0; j < startRow + checkRows; j++) {
                    for (int k = startCol; k < startCol + checkCols; k++) {
                        Log.d(LOG_TAG, "setUpMineField else block plant mine j ["+j+"] k ["+k+"]");
                        if (!mTitles[j][k].isMine()) {
                            mTitles[j][k].updateSurroundingMineCount();
                            Log.d(LOG_TAG, "setUpMineField else block plant mine j [" + j + "] k [" + k + "] updateSurroundingMineCount");
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

        for (int i = startRow; i < startRow + checkRows; i++ ) {
            for (int j = startCol; j < startCol +checkCols; j++) {

                if (mTitles[i][j].isCovered()) {
                    uncoverTiles(i,j);
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

    public void endGame()
    {
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
        timer.removeCallbacks(updateTimer);
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
