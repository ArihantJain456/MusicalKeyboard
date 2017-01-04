package com.example.arihantjain.musicalkeyboard;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Arihant Jain on 1/1/2017.
 */

public class Board extends SurfaceView implements Runnable {
    Thread thread = null;
    boolean canDraw;
    Canvas canvas;
    Rect back;
    SurfaceHolder surfaceHolder;
    Bitmap background,nn;
    Paint black_paint,white_paint,dark_grey,light_grey;
    public Board(Context context) {
        super(context);
        surfaceHolder = getHolder();
        background = BitmapFactory.decodeResource(getResources(),R.drawable.white);
        nn = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);
    }

    @Override
    public void run() {
        prePaint();
        setBackground();
        while(canDraw){
            if(!surfaceHolder.getSurface().isValid()){
                continue;
            }
            canvas = surfaceHolder.lockCanvas();
            canvas.drawRect(back,white_paint);
            canvas.drawBitmap(background,0,0,null);
            canvas.drawBitmap(nn,0,0,null);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void resume() {
        canDraw = true;
        thread = new Thread(this);
        thread.start();

    }

    public void pause() {

        canDraw = false;
        while (true) {
            try {
                thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }
    }
    private void prePaint(){
        black_paint = new Paint();
        black_paint.setColor(Color.BLACK);
        black_paint.setStyle(Paint.Style.FILL);

        white_paint = new Paint();
        white_paint.setColor(Color.WHITE);
        white_paint.setStyle(Paint.Style.FILL);

        dark_grey = new Paint();
        dark_grey.setColor(Color.DKGRAY);
        dark_grey.setStyle(Paint.Style.FILL);

        light_grey = new Paint();
        light_grey.setColor(Color.LTGRAY);
        light_grey.setStyle(Paint.Style.FILL);
    }
    private void setBackground(){
        back = new Rect(0,0,getWidth(),getHeight());
        System.out.print(getHeight()+ " " + getWidth());
    }

}
