package com.example.gobang;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.SoundPool;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
public class GobangActivity extends AppCompatActivity {
    private static ImageView image;
    private Thread thread;
    public static int times=0;
    public static int huiqishus=0;
    public static String TIME="时间:";
    public static String HUICHESS="悔棋:";
    public static boolean game=true;
    public static boolean start=false;
    private Boolean isGreen=true;
    Button huiqi;
    private static TextView huiqitext;
    private static TextView time;
    private static double times2=0;
    Button re;
    private SoundPool soundPool;
    private int soundID;
    private GobangView view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gobang);
        huiqishus=0;
        ImageView back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final ImageView music=(ImageView) findViewById(R.id.music);
        soundPool=new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
        soundID = soundPool.load(this, R.raw.clicks, 1);
        music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isGreen){
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
        });
        huiqi=(Button) findViewById(R.id.huiqi);
        re=(Button) findViewById(R.id.re);
        view=(GobangView) findViewById(R.id.Gobang_view);
        huiqi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundID, 0.5f, 0.5f, 0, 0, 1);
                view.huiqi();
            }
        });
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundPool.play(soundID, 0.5f, 0.5f, 0, 0, 1);
                view.re();
            }
        });
        time=(TextView) findViewById(R.id.time);
        huiqitext=(TextView) findViewById(R.id.huiqishu);
        image=(ImageView) findViewById(R.id.chess);
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(game) {
                    if(start){
                        start=false;
                        gameStart();
                    }
                    if (GobangView.first) {
                        try {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    time.setText(TIME + (int)(times+times2) + "秒");
                                }
                            });
                            Thread.sleep(100);
                            times2+=0.1;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
        if(GobangView.aiGame){
            start=true;
        }
        TextPaint textPaint=time.getPaint();
        textPaint.setFakeBoldText(true);
        textPaint=huiqi.getPaint();
        textPaint.setFakeBoldText(true);
        TextView dqzz=(TextView) findViewById(R.id.zhizi);
        textPaint=dqzz.getPaint();
        textPaint.setFakeBoldText(true);
    }
    public static void updatehuiqi(){
        huiqitext.setText(HUICHESS+huiqishus);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        times=0;
        game=false;
    }
    public static void setImage(Bitmap bitmap){
        image.setImageBitmap(bitmap);
    }
    public static void time_start(){
        time.setText(TIME + (int)(times+1) + "秒");
    }
    private void gameStart(){
        ItemDialogFragment itemsDialogFragment = new ItemDialogFragment();
        String[] items = {"▶选择先手", "▶选择后手", "▶返回"};
        itemsDialogFragment.setCancelable(false);
        itemsDialogFragment.show("请选择先后手", items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        break;
                    case 1:
                        GobangView.startGame=true;
                        break;
                    case 2:
                        finish();
                        game=false;
                        break;
                }
            }
        }, getFragmentManager());
    }
}
