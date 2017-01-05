package com.example.arihantjain.musicalkeyboard;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class MusicalBoard extends AppCompatActivity {
Board mBoard;
    int Mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Mode = intent.getIntExtra("mode",0);
        if(Mode == MainActivity.RECORD){
            MainActivity.recording = new ArrayList<>();
        }
        mBoard = new Board(this,Mode);
        setContentView(mBoard);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBoard.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBoard.resume();
    }
}
