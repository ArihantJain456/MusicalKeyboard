package com.example.arihantjain.musicalkeyboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final int PLAY = 0;
    public static final int RECORD = 1;
    public static final int VIEW_RECORD = 2;
    Button play,record,viewRecord,exit;
    public static ArrayList<String> recording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPref();
        play = (Button)findViewById(R.id.play_btn);
        record = (Button)findViewById(R.id.record_btn);
        viewRecord = (Button)findViewById(R.id.viewRecording_btn);
        exit = (Button)findViewById(R.id.exit_btn);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MusicalBoard.class);
                intent.putExtra("mode",PLAY);
                startActivity(intent);
            }
        });
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MusicalBoard.class);
                intent.putExtra("mode",RECORD);
                startActivityForResult(intent,RECORD);
            }
        });
        viewRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(recording);
                if(recording!=null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Last Recorded Sound")
                            .setMessage(recording.toString())
                            .setNeutralButton("Play", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(MainActivity.this, MusicalBoard.class);
                                    intent.putExtra("mode", VIEW_RECORD);
                                    startActivity(intent);
                                }
                            }).show();
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Last Recorded Sound")
                            .setMessage("No recording found")
                            .show();
                }
            }
        });
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void savePref(String key,ArrayList<String> list){
        String recordKeyString = list.get(0);
        for(int i =1; i<list.size();i++){
            recordKeyString+=("," + list.get(i));
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key,recordKeyString);
        editor.commit();
    }
    private void loadPref(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String recordKeyString = sharedPreferences.getString("recording",null);
        if(recordKeyString!=null){
            recording = new ArrayList<>();
            String [] records = recordKeyString.split(",");
            for(int i=0;i<records.length;i++){
                recording.add(records[i]);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println(requestCode);
        if(resultCode == RESULT_OK){
            if(requestCode == RECORD){
                recording = data.getStringArrayListExtra("recordedKeys");
                System.out.println(requestCode);
                savePref("recording",recording);
            }
        }
    }
}
