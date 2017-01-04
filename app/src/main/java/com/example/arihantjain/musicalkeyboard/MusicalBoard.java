package com.example.arihantjain.musicalkeyboard;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MusicalBoard extends AppCompatActivity {
Board mBoard;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBoard = new Board(this);
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
