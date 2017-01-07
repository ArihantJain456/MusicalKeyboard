package com.example.arihantjain.musicalkeyboard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v4.view.ViewCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

public class Board extends SurfaceView implements Runnable {
    int HEIGHT;
    int WIDTH;
    Paint black_line;
    Paint black_paint,text_paint;
    boolean canDraw;
    Canvas canvas;
    Context context;
    Paint dark_grey;
    Paint light_grey;
    Path line;
    MediaPlayer mediaPlayer;
    int[] whiteNotes,blackNotes;
    SurfaceHolder surfaceHolder;
    Thread thread;
    long whenKeyPressed;
    Paint white_paint;
    int pre = -1,current;
    double framesPerSeconds,frameTimeSeconds,frameTimeMS,frameTimeNS;
    double tLF,tEOR,deltaT;
    ArrayList<Rect> whiteKeys,blackKeys,secondaryBlackKeys;
    ArrayList<Integer> pressedBlackKeys;
    int MODE;
    int keyCounter = 0;
    ArrayList<MediaPlayer> whiteTones;
    ArrayList<String> newRecording;
    ArrayList<String> recorded = MainActivity.recording;

    public Board(Context context,int mode) {
        super(context);
        MODE = mode;
        if(MODE == MainActivity.RECORD){
            newRecording = new ArrayList<>();
        }

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        WIDTH = point.x;
        HEIGHT = point.y;
        canDraw = false;
        this.thread = null;
        framesPerSeconds = 15;
        frameTimeSeconds = 1/framesPerSeconds;
        frameTimeMS = frameTimeSeconds*1000;
        frameTimeNS = frameTimeMS *1000000;
        blackNotes = new int[]{R.raw.note2s,R.raw.note4s, R.raw.note7s, R.raw.note9s, R.raw.note11s,};
        whiteNotes = new int[]{R.raw.note1s, R.raw.note3s, R.raw.note5s, R.raw.note6s, R.raw.note8s, R.raw.note10s, R.raw.note12s};
        this.context = context;
        this.surfaceHolder = getHolder();
        this.line = new Path();
        whiteKeys = new ArrayList<>();
        blackKeys = new ArrayList<>();
        secondaryBlackKeys = new ArrayList<>();
        pressedBlackKeys = new ArrayList<>();
        setUpTones();
        for (int i = 1; i < 7; i++) {
            setBlackKeys(i);
        }
    }

    public void run() {
        tLF = System.nanoTime();
        deltaT = 0;
        prePaint();
        if(MODE == MainActivity.VIEW_RECORD){
            playRecordedSound();
        }
        while (this.canDraw) {

            update(deltaT);
            if (!this.surfaceHolder.getSurface().isValid()) {
                continue;
            }
            this.canvas = this.surfaceHolder.lockCanvas();
            draw(this.canvas);


                this.surfaceHolder.unlockCanvasAndPost(this.canvas);

            tEOR = System.nanoTime();

            deltaT = frameTimeNS -(tEOR-tLF);
            try {if(deltaT>0) {
                thread.sleep((long) deltaT / 1000000);
            }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            tLF = System.nanoTime();
        }
    }

    private void update(double deltaT) {
        if(deltaT<0){
            deltaT = frameTimeNS;
        }
    }

    private void stopMediaPlayer() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.release();
            this.mediaPlayer = null;
        }
    }

    private void play(int r) {
        stopMediaPlayer();
        this.mediaPlayer = MediaPlayer.create(this.context, r);
        this.mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                System.out.println("Finishing sound");
            }
        });
    }

    public void resume() {
        this.canDraw = true;
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void pause() {
        this.canDraw = false;
        while (true) {
            try {
                this.thread.join();
                break;
            } catch (InterruptedException e) {
                e.printStackTrace();
                this.thread = null;
            }
        }
    }

    private void prePaint() {
        this.black_paint = new Paint();
        this.black_paint.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.black_paint.setStyle(Style.FILL);
        this.white_paint = new Paint();
        this.white_paint.setColor(-1);
        this.white_paint.setStyle(Style.FILL);
        this.dark_grey = new Paint();
        this.dark_grey.setColor(Color.DKGRAY);
        this.dark_grey.setStyle(Style.FILL);
        this.light_grey = new Paint();
        this.light_grey.setColor(Color.LTGRAY);
        this.light_grey.setStyle(Style.FILL);
        this.black_line = new Paint();
        this.black_line.setColor(ViewCompat.MEASURED_STATE_MASK);
        this.black_line.setStyle(Style.STROKE);
        this.black_line.setStrokeWidth(3.0f);
        text_paint = new Paint();
        text_paint.setColor(Color.GRAY);
        text_paint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
        text_paint.setTextSize(100);
    }

    private void drawPressedKey(int x, int y) {
        for(int i=0;i<blackKeys.size();i++){
            if(blackKeys.get(i).contains(x,y)){
                Rect rect1 = new Rect(blackKeys.get(i));
                secondaryBlackKeys.add(rect1);
                play(blackNotes[i]);
                if(MODE == MainActivity.RECORD) {
                    recordKey('a',i);
                }
                return;
            }
        }
        int posX = (x * 7) / this.WIDTH;
        Rect pressedWhiteKey = new Rect((this.WIDTH * posX) / 7, 0, ((this.WIDTH * posX) / 7) + (this.WIDTH / 7), this.HEIGHT);
        whiteKeys.add(pressedWhiteKey);
        MediaPlayer mediaPlayer = whiteTones.get(posX);
        if(mediaPlayer.isPlaying()){
            mediaPlayer = MediaPlayer.create(context,whiteNotes[posX]);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.start();
        }
        if(MODE == MainActivity.RECORD) {
            recordKey('A',posX);
        }
    }

    private void setBlackKeys(int pos){

        float x = (((float) (this.WIDTH * pos)) * 1.0f) / 7.0f;
        float y = ((float) this.HEIGHT) * 1.0f;
        Rect blackKey = new Rect(((int) x) - (this.WIDTH / 35), 0, ((int) x) + (this.WIDTH / 35), ((int) y) / 2);
        if(pos!=3)
            blackKeys.add(blackKey);
    }
    private void drawLine(int pos) {
        line = new Path();
        float x = (((float) (this.WIDTH * pos)) * 1.0f) / 7.0f;
        float y = ((float) this.HEIGHT) * 1.0f;
        this.line.moveTo(x, 0.0f);
        this.line.lineTo(x, y);

    }

    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawColor(Color.WHITE);
        if (!whiteKeys.isEmpty()) {
            long elapsed = System.currentTimeMillis() - this.whenKeyPressed;
            for(Rect rect:whiteKeys){
                canvas.drawRect(rect,light_grey);
            }
            if (elapsed > 10) {
                whiteKeys.remove(0);
                whenKeyPressed = System.currentTimeMillis();
            }
        }
        for (int i = 1; i < 7; i++) {
            drawLine(i);
            canvas.drawPath(this.line, this.black_line);
    }
        for(Rect rect:blackKeys){
            canvas.drawRect(rect,black_paint);
        }
        if(!secondaryBlackKeys.isEmpty()){
            long elapsed = System.currentTimeMillis() - this.whenKeyPressed;
            for(Rect rect:secondaryBlackKeys){
                canvas.drawRect(rect,dark_grey);
            }
            if (elapsed > 10) {
                secondaryBlackKeys.remove(0);
                whenKeyPressed = System.currentTimeMillis();
            }
        }
        for(int i=0;i<7;i++){
            char ch = 'A';
            ch+=i;
            canvas.drawText(ch+"",(i)*WIDTH/7 +WIDTH/14-50,HEIGHT*2/3,text_paint);
        }
        for(int i=1;i<6;i++){
            char ch = 'a'-1;
            int xPos;
            ch+=i;
            if(i<=2){
                xPos = (i)*WIDTH/7 - 10;
            }else {
                xPos = (i+1)*WIDTH/7 - 10;
            }
            canvas.drawText(ch+"",xPos,HEIGHT/3,text_paint);
        }
    }

    public boolean onTouchEvent(MotionEvent event){
        if(MODE!=MainActivity.VIEW_RECORD) {
            if (event.getAction() == 0) {
                this.whenKeyPressed = System.currentTimeMillis();
                drawPressedKey((int) event.getX(), (int) event.getY());
                System.out.println("Down");
                return true;
            }
            if(event.getAction() == MotionEvent.ACTION_UP){
                System.out.println("UP");
                return true;
            }
            if(event.getAction() == MotionEvent.ACTION_MOVE){
                current = (int)event.getX()*7/WIDTH;
                if(current!=pre){
                    pre = current;
                    this.whenKeyPressed = System.currentTimeMillis();
                    drawPressedKey((int) event.getX(), (int) event.getY());

                }
                System.out.println("Move");
                return true;
            }
        }
        return super.onTouchEvent(event);
    }
    private void recordKey(char ch,int i){
        ch += i;
        newRecording.add(ch+"");
        keyCounter++;
        if (keyCounter == 10) {
            Intent recordedDataIntent = new Intent();
            recordedDataIntent.putStringArrayListExtra("recordedKeys",newRecording);
            System.out.println(newRecording);
            ((Activity)context).setResult(Activity.RESULT_OK,recordedDataIntent);
            ((Activity) context).finish();
        }
    }
    private void playRecordedSound(){

        int res;
        char[] keyPressed = recorded.get(keyCounter).toCharArray();
        if(keyPressed[0] <='G'){
            res = whiteNotes[keyPressed[0] - 'A'];
        }
        else {
            res = blackNotes[keyPressed[0] - 'a'];
        }
        mediaPlayer = MediaPlayer.create(context,res);
        mediaPlayer.setOnCompletionListener(onCompletionListener);
        mediaPlayer.start();
    }
    MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            keyCounter++;
            if (keyCounter < recorded.size()) {
                int res;
                char[] keyPressed = recorded.get(keyCounter).toCharArray();
                if (keyPressed[0] <= 'G') {
                    int pos = keyPressed[0] - 'A';
                    res = whiteNotes[pos];
                    Rect pressedWhiteKey = new Rect((WIDTH * pos) / 7, 0, ((WIDTH * pos) / 7) + (WIDTH / 7),HEIGHT);
                    whiteKeys.add(pressedWhiteKey);
                } else {
                    int pos = keyPressed[0] - 'a';

                    res = blackNotes[pos];
                            Rect rect1 = new Rect(blackKeys.get(pos));
                            secondaryBlackKeys.add(rect1);
                }
                mediaPlayer = MediaPlayer.create(context, res);
                mediaPlayer.setOnCompletionListener(onCompletionListener);
                mediaPlayer.start();
            }
        }
    };
    private void setUpTones(){
        whiteTones = new ArrayList<>();
        for(int i=0;i<whiteNotes.length;i++){
            MediaPlayer player = new MediaPlayer();
            player = MediaPlayer.create(context,whiteNotes[i]);
            whiteTones.add(player);
        }
    }
}