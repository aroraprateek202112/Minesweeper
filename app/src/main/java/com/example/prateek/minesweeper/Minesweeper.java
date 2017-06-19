package com.example.prateek.minesweeper;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class Minesweeper extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minesweeper);

        Button newButton = (Button)findViewById(R.id.new_game_btn);
        newButton.setOnClickListener(this);

        Button continueButton = (Button)findViewById(R.id.continue_btn);
        continueButton.setOnClickListener(this);

        Button ruleButton = (Button)findViewById(R.id.rule_btn);
        ruleButton.setOnClickListener(this);

        Button exitButton = (Button)findViewById(R.id.exit_btn);
        exitButton.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.new_game_btn :
                openNewGameDialog();
                break;
            case R.id.continue_btn :
                break;
            case R.id.rule_btn :
                break;
            case R.id.exit_btn :
                finish();
                break;
            default:
                break;
        }

    }

    private void openNewGameDialog() {

        new AlertDialog.Builder(this).setTitle("Difficulty")
                .setItems(R.array.difficulty, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startNewGame(i);
                    }
                }).show();
    }

    private void startNewGame(int i) {
        Intent intent = new Intent(this, Game.class);
        intent.putExtra(Game.KEY_DIFFICULTY, i);
        startActivity(intent);

    }
}
