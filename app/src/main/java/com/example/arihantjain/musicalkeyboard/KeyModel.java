package com.example.arihantjain.musicalkeyboard;

import android.content.Context;
import android.graphics.Rect;
import android.media.MediaPlayer;

/**
 * Created by Arihant Jain on 1/7/2017.
 */

public class KeyModel {
    int position;
    char color;
    Rect rect;
    MediaPlayer player;

    public Rect getRect() {
        return rect;
    }

    public void setRect(Rect rect) {
        this.rect = rect;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public char getColor() {
        return color;
    }

    public void setColor(char color) {
        this.color = color;
    }

    public MediaPlayer getPlayer() {
        return player;
    }

    public void setPlayer(MediaPlayer player) {
        this.player = player;
    }
}
