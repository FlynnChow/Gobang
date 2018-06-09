package com.example.gobang;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Button scgame;
    private Button aigame;
    private Button black;
    private SoundPool soundPool;
    private int soundID;
    public int times=0;
    public final String TIME="时间:";
    public  static MediaPlayer mediaPlayer;
    private ImageView music;
    private Boolean isGreen=true;
    TextView time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }else{
            initMediaPlayer();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
        scgame=(Button) findViewById(R.id.second_game);
        aigame=(Button) findViewById(R.id.ai_game);
        black=(Button) findViewById(R.id.black);
        music=(ImageView) findViewById(R.id.music_main);
        scgame.setOnClickListener(this);
        aigame.setOnClickListener(this);
        black.setOnClickListener(this);
        music.setOnClickListener(this);
        inint();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.second_game:
                soundPool.play(soundID,0.4f, 0.4f, 1, 0, 1);
                GobangActivity.game=true;
                GobangView.aiGame=false;
                startActivity(new Intent(MainActivity.this,GobangActivity.class));
                break;
            case R.id.ai_game:
                soundPool.play(soundID,0.5f, 0.5f, 1, 0, 1);
                GobangActivity.game=true;
                GobangView.aiGame=true;
                startActivity(new Intent(MainActivity.this,GobangActivity.class));
                break;
            case R.id.black:
                soundPool.play(soundID,0.5f, 0.5f, 1, 0, 1);
                finish();
                break;
            case R.id.music_main:
                if(isGreen){
                    soundPool.play(soundID, 0.5f, 0.5f, 0, 0, 1);
                    MainActivity.mediaPlayer.pause();
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.musicread);
                    music.setImageBitmap(bitmap);
                    isGreen=false;
                }else{
                    MainActivity.mediaPlayer.start();
                    Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.music);
                    music.setImageBitmap(bitmap);
                    isGreen=true;
                }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
        switch (requestCode){
            case 1:
                if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    initMediaPlayer();
                    mediaPlayer.start();
                    mediaPlayer.setLooping(true);
                }
                else{
                    Toast.makeText(MainActivity.this,"获取权限失败",Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }
    private void initMediaPlayer(){
        try{
            mediaPlayer=MediaPlayer.create(getApplicationContext(),R.raw.music);
            mediaPlayer.prepare();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Override
    protected void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }
    private void inint(){
        soundPool=new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundID = soundPool.load(this, R.raw.clicks, 1);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(800);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                scgame.setVisibility(View.VISIBLE);
                            }
                        });
                        Thread.sleep(120);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                aigame.setVisibility(View.VISIBLE);
                            }
                        });
                        Thread.sleep(120);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                black.setVisibility(View.VISIBLE);
                            }
                        });
                        Thread.sleep(120);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                music.setVisibility(View.VISIBLE);
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }).start();
    }
}
