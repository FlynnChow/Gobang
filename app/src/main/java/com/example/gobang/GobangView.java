package com.example.gobang;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GobangView extends View {
    private static final String TAG = "GobangView";
    public static final int WHITE_CHESS=1;
    public static final int BLACK_CHESS=2;
    public static boolean Game=true;
    public static final int NO_WIN=5;
    public static boolean startGame=false;
    public static boolean aiGame=false;
    public static Boolean first=false;
    private boolean isHuiqi=false;
    private int downX;
    private int downY;
    public static boolean aiIsBlack=false;
    private Point p;
    private Point p_ai=null;
    private boolean GameOver=false;
    private float rowSize = 3 * 1.0f / 4;
    private boolean isBlack=true;
    private Rect rect;
    private  List<Point> myWhiteChess=new ArrayList<>();
    private  List<Point> myBlackChess=new ArrayList<>();
    private int width;
    private String sz[]={"15","14","13","12","11","10"," 9"," 8"," 7"," 6"," 5"," 4"," 3"," 2"," 1"};
    private String zm[]={"A","B","C","D","E"," F","G","H"," I","J"," K"," L","M","N","O"};
    private Bitmap blackChess;
    private Bitmap whiteChess;
    private Bitmap spin;
    public GobangView(Context context) {
        super(context);
        init();
    }
    public GobangView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        /*
        棋盘绘制
         */
        super.onDraw(canvas);
        Paint paint=new Paint();
        paint.setStrokeWidth(5);
        for(int i=1;i<=15;i++){
            canvas.drawLine(width+width/2,width*i+width/2,width*15+width/2,width*i+width/2,paint);//————
            canvas.drawLine(width*i+width/2,width+width/2,width*i+width/2,width*15+width/2,paint);//|||||||
        }
        canvas.drawCircle(width*8+width/2,width*8+width/2,width/5,paint);
        canvas.drawCircle(width*4+width/2,width*12+width/2,width/5,paint);
        canvas.drawCircle(width*12+width/2,width*4+width/2,width/5,paint);
        canvas.drawCircle(width*4+width/2,width*4+width/2,width/5,paint);
        canvas.drawCircle(width*12+width/2,width*12+width/2,width/5,paint);
        paint.setTextSize(55);
        paint.setFakeBoldText(true);
        for(int i=0;i<15;i++){
            canvas.drawText(sz[i],width/5+width/2,width+width/5+width*i+width/2,paint);
            canvas.drawText(zm[i],width+width*i-width/4+width/2,width*15+width/2+width/8+width/2,paint);
        }
        paint.setTextSize(85);
        canvas.drawText("五子棋",width*6+width/2+width/2,width/2+width/3+width/2,paint);
        drawPieces(canvas);
        if(isBlack){
            GobangActivity.setImage(blackChess);
        }else{
            GobangActivity.setImage(whiteChess);
        }
    }
    private void init(){
        WindowManager m=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        blackChess= BitmapFactory.decodeResource(getResources(),R.drawable.black);
        whiteChess=BitmapFactory.decodeResource(getResources(),R.drawable.white);
        spin=BitmapFactory.decodeResource(getResources(),R.drawable.spin);
        width=m.getDefaultDisplay().getWidth();
        width= width/16;
        int width_chess=(int)(width*rowSize);
        blackChess=Bitmap.createScaledBitmap(blackChess,width_chess,width_chess,false);
        whiteChess=Bitmap.createScaledBitmap(whiteChess,width_chess,width_chess,false);
        spin=Bitmap.createScaledBitmap(spin,width*2,width*2,false);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(Game){
                    try{
                        Thread.sleep(200);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    if(startGame){
                            myBlackChess.add(new Point(8,8));
                            p_ai=new Point(8,8);
                            isBlack=!isBlack;
                            startGame=false;
                            invalidate();
                    }
                }
            }
        }).start();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(GameOver){
            Toast.makeText(getContext(),"游戏已结束",Toast.LENGTH_SHORT).show();
            return true;
        }
        if(isHuiqi){
            isHuiqi=false;
        }
        if(!first)
            first=true;
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                downX=(int)event.getX();
                downY=(int)event.getY();
                p=getValidPoint(downX ,downY);
                if(p.x<1||p.x>15||p.y<1||p.y>15){
                    return true;
                }
                if(myBlackChess.contains(p)|myWhiteChess.contains(p)){
                    return false;
                }
                if(isBlack){
                    myBlackChess.add(p);
                }else{
                    myWhiteChess.add(p);
                }
                if(isBlack){
                    checkGame(myBlackChess);
                }else{
                    checkGame(myWhiteChess);
                }
                isBlack=!isBlack;
                invalidate();
                if(aiGame&&!GameOver){
                    if(isBlack){
                        p_ai=ai(myWhiteChess,myBlackChess);
                        invalidate();
                        checkGame(myBlackChess);
                        isBlack=!isBlack;
                    }else{
                        p_ai=ai(myBlackChess,myWhiteChess);
                        invalidate();
                        checkGame(myWhiteChess);
                        isBlack=!isBlack;
                    }
                }
                break;
        }
        return true;
    }
    private Point getValidPoint(int x, int y) {

        return new Point((int) (x / width), (int) (y / width));
    }
    private void drawPieces(Canvas canvas){
        for(int i=0;i<myWhiteChess.size();i++){
            Point whitePoint=myWhiteChess.get(i);
            canvas.drawBitmap(whiteChess, (whitePoint.x + (1 - rowSize) / 2) * width, (whitePoint.y + (1 - rowSize) / 2) * width, null);
        }
        for(int i=0;i<myBlackChess.size();i++){
            Point blackPoint=myBlackChess.get(i);
            canvas.drawBitmap(blackChess, (blackPoint.x + (1 - rowSize) / 2) * width, (blackPoint.y + (1 - rowSize) / 2) * width, null);
        }
        if(p_ai!=null) canvas.drawBitmap(spin, p_ai.x*width-width/2, p_ai.y * width-width/2, null);
    }
    @Override
    protected void onMeasure(int w,int h){
        int widthSize=MeasureSpec.getSize(w);
        int widthMode=MeasureSpec.getMode(w);
        int heightSize=MeasureSpec.getSize(h);
        int heithtMode=MeasureSpec.getMode(h);
        int wid=Math.min(widthSize,heightSize);
        if(widthMode==MeasureSpec.UNSPECIFIED){
            wid=heightSize;
        }else if(heithtMode==MeasureSpec.UNSPECIFIED){
            wid=widthSize;
        }
        setMeasuredDimension(wid,wid+width/2);
    }
    private void checkGame(List<Point> ps){
        for(Point p:ps){
            if(checkWin(p,ps)){
                first=false;
                if (isBlack){
                    Toast.makeText(getContext(),"黑棋胜",Toast.LENGTH_LONG).show();
                    GameOver=true;
                    first=false;
                }else{
                    Toast.makeText(getContext(),"白棋胜", Toast.LENGTH_LONG).show();
                    GameOver=true;
                    first=false;
                }
            }
        }
    }
    private  boolean checkWin(Point p,List<Point> ps) {
        int x = p.x;
        int y = p.y;
        /*横*/
        int count=1;
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x-i,y))){
                count++;
            }else {
                break;
            }
        }
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x+i,y))){
                count++;
            }else {
                break;
            }
        }
        if(count>=5){
            return true;
        }
        /*竖*/
        count=1;
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x,y+i))){
                count++;
            }else {
                break;
            }
        }
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x,y-i))){
                count++;
            }else {
                break;
            }
        }
        if(count>=5){
            return true;
        }
        /*/*/
        count=1;
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x+i,y-i))){
                count++;
            }else {
                break;
            }
        }
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x-i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count>=5){
            return true;
        }
        /*|*/
        count=1;
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x-i,y-i))){
                count++;
            }else {
                break;
            }
        }
        for(int i=1;i<5;i++){
            if(ps.contains(new Point(x+i,y+i))){
                count++;
            }else {
                break;
            }
        }
        if(count>=5){
            return true;
        }
        return false;
    }
    public void huiqi(){
        if(aiGame){
            Toast.makeText(getContext(),"此功能暂不开放",Toast.LENGTH_SHORT).show();
            return;
        }
        if (!first){
            Toast.makeText(getContext(),"还未下棋",Toast.LENGTH_SHORT).show();
            return;
        }
        if(GameOver){
            Toast.makeText(getContext(),"游戏已结束",Toast.LENGTH_SHORT).show();
            return;
        }
        if(isHuiqi){
            Toast.makeText(getContext(),"只能悔棋一步",Toast.LENGTH_SHORT).show();
            return;
        }else isHuiqi=true;
        if(!isBlack){
            GobangActivity.huiqishus++;
            myBlackChess.remove(p);
            invalidate();
            isBlack=!isBlack;
            Toast.makeText(getContext(),"悔棋成功",Toast.LENGTH_SHORT).show();
        }else {
            GobangActivity.huiqishus++;
            myWhiteChess.remove(p);
            invalidate();
            isBlack=!isBlack;
            Toast.makeText(getContext(),"悔棋成功",Toast.LENGTH_SHORT).show();
        }
        GobangActivity.updatehuiqi();
    }
    public void re(){
        p_ai=null;
        if(aiGame){
            GobangActivity.start=true;
        }
        myBlackChess.clear();
        myWhiteChess.clear();
        aiIsBlack=false;
        GobangActivity.times=-1;
        GobangActivity.huiqishus=0;
        GobangActivity.updatehuiqi();
        GobangActivity.time_start();
        first=false;
        isBlack=true;
        invalidate();
        GameOver=false;

    }
    private int XY(int x,int y){
        if(x<1||x>15){
            return 1;
        }
        else if(y<1||y>15){
            return 1;
        }
        return 0;
    }
    private Point ai(List<Point> pr,List<Point> ai){
        int i;
        for(Point p:ai){
            int x=p.x;
            int y=p.y;
            //4子
            if(ai.contains(new Point(x+1,y))&&ai.contains(new Point(x+2,y))&&ai.contains(new Point(x+3,y))&&!pr.contains(new Point(x+4,y))&&!ai.contains(new Point(x+4,y))&&XY(x+4,y)==0){
                ai.add(new Point(x+4,y));return new Point(x+4,y);
            }
            if(ai.contains(new Point(x,y+1))&&ai.contains(new Point(x,y+2))&&ai.contains(new Point(x,y+3))&&!pr.contains(new Point(x,y+4))&&!ai.contains(new Point(x,y+4))&&XY(x,y+4)==0){
                ai.add(new Point(x,y+4));return new Point(x,y+4);
            }
            if(ai.contains(new Point(x+1,y+1))&&ai.contains(new Point(x+2,y+2))&&ai.contains(new Point(x+3,y+3))&&!pr.contains(new Point(x+4,y+4))&&!ai.contains(new Point(x+4,y+4))&&XY(x+4,y+4)==0){
                ai.add(new Point(x+4,y+4));return new Point(x+4,y+4);
            }
            if(ai.contains(new Point(x+1,y-1))&&ai.contains(new Point(x+2,y-2))&&ai.contains(new Point(x+3,y-3))&&!pr.contains(new Point(x+4,y-4))&&!ai.contains(new Point(x+4,y-4))&&XY(x+4,y-4)==0){
                ai.add(new Point(x+4,y-4));return new Point(x+4,y-4);
            }
            if(ai.contains(new Point(x-1,y))&&ai.contains(new Point(x-2,y))&&ai.contains(new Point(x-3,y))&&!pr.contains(new Point(x-4,y))&&!ai.contains(new Point(x-4,y))&&XY(x-4,y)==0){
                ai.add(new Point(x-4,y));return new Point(x-4,y);
            }
            if(ai.contains(new Point(x,y-1))&&ai.contains(new Point(x,y-2))&&ai.contains(new Point(x,y-3))&&!pr.contains(new Point(x,y-4))&&!ai.contains(new Point(x,y-4))&&XY(x,y-4)==0){
                ai.add(new Point(x,y-4));return new Point(x,y-4);
            }
            if(ai.contains(new Point(x-1,y-1))&&ai.contains(new Point(x-2,y-2))&&ai.contains(new Point(x-3,y-3))&&!pr.contains(new Point(x-4,y-4))&&!ai.contains(new Point(x-4,y-4))&&XY(x-4,y-4)==0){
                ai.add(new Point(x-4,y-4));return new Point(x-4,y-4);
            }
            if(ai.contains(new Point(x-1,y+1))&&ai.contains(new Point(x-2,y+2))&&ai.contains(new Point(x-3,y+3))&&!pr.contains(new Point(x-4,y+4))&&!ai.contains(new Point(x-4,y+4))&&XY(x-4,y+4)==0){
                ai.add(new Point(x-4,y+4));return new Point(x-4,y+4);
            }
        }

        for(Point p:ai){
            int x=p.x;
            int y=p.y;
            if(ai.contains(new Point(x+1,y))&&ai.contains(new Point(x+3,y))&&ai.contains(new Point(x+2,y))&&pr.contains(new Point(x+2,y))&&ai.contains(new Point(x-1,y))&&pr.contains(new Point(x-1,y))&&ai.contains(new Point(x+4,y))&&pr.contains(new Point(x+4,y))&&XY(x+2,y)==0){
                ai.add(new Point(x+2,y));return new Point(x+2,y);
            }
            if(ai.contains(new Point(x-1,y))&&ai.contains(new Point(x-3,y))&&ai.contains(new Point(x-2,y))&&pr.contains(new Point(x-2,y))&&ai.contains(new Point(x+1,y))&&pr.contains(new Point(x+1,y))&&ai.contains(new Point(x-4,y))&&pr.contains(new Point(x-4,y))&&XY(x-2,y)==0){
                ai.add(new Point(x-2,y));return new Point(x-2,y);
            }
            if(ai.contains(new Point(x,y+1))&&ai.contains(new Point(x,y+3))&&ai.contains(new Point(x,y+2))&&pr.contains(new Point(x,y+2))&&ai.contains(new Point(x,y-1))&&pr.contains(new Point(x,y-1))&&ai.contains(new Point(x,y+4))&&pr.contains(new Point(x,y+4))&&XY(x,y+2)==0){
                ai.add(new Point(x,y+2));return new Point(x,y+2);
            }
            if(ai.contains(new Point(x,y-1))&&ai.contains(new Point(x,y-3))&&ai.contains(new Point(x,y-2))&&pr.contains(new Point(x,y-2))&&ai.contains(new Point(x,y+1))&&pr.contains(new Point(x,y+1))&&ai.contains(new Point(x,y-4))&&pr.contains(new Point(x,y-4))&&XY(x,y-2)==0){
                ai.add(new Point(x,y-2));return new Point(x,y-2);
            }
            if(ai.contains(new Point(x+1,y+1))&&ai.contains(new Point(x+3,y+3))&&ai.contains(new Point(x+2,y+2))&&pr.contains(new Point(x+2,y+2))&&ai.contains(new Point(x-1,y-1))&&pr.contains(new Point(x-1,y-1))&&ai.contains(new Point(x+4,y+4))&&pr.contains(new Point(x+4,y+4))&&XY(x+2,y+2)==0){
                ai.add(new Point(x+2,y+2));return new Point(x+2,y+2);
            }
            if(ai.contains(new Point(x-1,y-1))&&ai.contains(new Point(x-3,y-3))&&ai.contains(new Point(x-2,y-2))&&pr.contains(new Point(x-2,y-2))&&ai.contains(new Point(x+1,y+1))&&pr.contains(new Point(x+1,y+1))&&ai.contains(new Point(x-4,y-4))&&pr.contains(new Point(x-4,y-4))&&XY(x-2,y-2)==0){
                ai.add(new Point(x-2,y-2));return new Point(x-2,y-2);
            }
            if(ai.contains(new Point(x+1,y-1))&&ai.contains(new Point(x+3,y-3))&&ai.contains(new Point(x+2,y-2))&&pr.contains(new Point(x+2,y-2))&&ai.contains(new Point(x-1,y+1))&&pr.contains(new Point(x-1,y+1))&&ai.contains(new Point(x+4,y-4))&&pr.contains(new Point(x+4,y-4))&&XY(x+2,y-2)==0){
                ai.add(new Point(x+2,y-2));return new Point(x+2,y-2);
            }
            if(ai.contains(new Point(x-1,y+1))&&ai.contains(new Point(x-3,y+3))&&ai.contains(new Point(x-2,y+2))&&pr.contains(new Point(x-2,y+2))&&ai.contains(new Point(x+1,y-1))&&pr.contains(new Point(x+1,y-1))&&ai.contains(new Point(x-4,y+4))&&pr.contains(new Point(x-4,y+4))&&XY(x-2,y+2)==0){
                ai.add(new Point(x-2,y+2));return new Point(x-2,y+2);
            }
        }
        for (Point p:ai){
            int x=p.x;
            int y=p.y;
            //三子
            if(ai.contains(new Point(x+1,y))&&ai.contains(new Point(x-1,y))&&!pr.contains(new Point(x+2,y))&&!pr.contains(new Point(x-2,y))&&!ai.contains(new Point(x+2,y))&&!ai.contains(new Point(x-2,y))&&XY(x+2,y)==0&&XY(x-2,y)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y));return new Point(x+2,y);
                }else{
                    ai.add(new Point(x-2,y));return new Point(x-2,y);
                }
            }
            if(ai.contains(new Point(x,y+1))&&ai.contains(new Point(x,y-1))&&!pr.contains(new Point(x,y+2))&&!pr.contains(new Point(x,y-2))&&ai.contains(new Point(x,y+2))&&!ai.contains(new Point(x,y-2))&&XY(x,y+2)==0&&XY(x,y-2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x,y+2));return new Point(x,y+2);
                }else{
                    ai.add(new Point(x,y-2));return new Point(x,y-2);
                }
            }
            if(ai.contains(new Point(x+1,y+1))&&ai.contains(new Point(x-1,y-1))&&!pr.contains(new Point(x+2,y+2))&&!pr.contains(new Point(x-2,y-2))&&!ai.contains(new Point(x+2,y+2))&&!ai.contains(new Point(x-2,y-2))&&XY(x+2,y+2)==0&&XY(x-2,y-2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y+2));return new Point(x+2,y+2);
                }else{
                    ai.add(new Point(x-2,y-2));return new Point(x-2,y-2);
                }
            }
            if(ai.contains(new Point(x+1,y-1))&&ai.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x+2,y-2))&&!pr.contains(new Point(x-2,y+2))&&!ai.contains(new Point(x+2,y-2))&&!ai.contains(new Point(x-2,y+2))&&XY(x+2,y-2)==0&&XY(x-2,y+2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y-2));return new Point(x+2,y-2);
                }else{
                    ai.add(new Point(x-2,y+2));return new Point(x-2,y+2);
                }
            }
        }
        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            //对方三子
            if(pr.contains(new Point(x+1,y))&&pr.contains(new Point(x-1,y))&&!ai.contains(new Point(x+2,y))&&!ai.contains(new Point(x-2,y))&&!pr.contains(new Point(x+2,y))&&!pr.contains(new Point(x-2,y))&&XY(x+2,y)==0&&XY(x-2,y)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y));return new Point(x+2,y);
                }else{
                    ai.add(new Point(x-2,y));return new Point(x-2,y);
                }
            }
            if(pr.contains(new Point(x,y+1))&&pr.contains(new Point(x,y-1))&&!ai.contains(new Point(x,y+2))&&!ai.contains(new Point(x,y-2))&&!pr.contains(new Point(x,y+2))&&!pr.contains(new Point(x,y-2))&&XY(x,y+2)==0&&XY(x,y-2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x,y+2));return new Point(x,y+2);
                }else{
                    ai.add(new Point(x,y-2));return new Point(x,y-2);
                }
            }
            if(pr.contains(new Point(x+1,y+1))&&pr.contains(new Point(x-1,y-1))&&!ai.contains(new Point(x+2,y+2))&&!ai.contains(new Point(x-2,y-2))&&!pr.contains(new Point(x+2,y+2))&&!pr.contains(new Point(x-2,y-2))&&XY(x+2,y+2)==0&&XY(x-2,y-2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y+2));return new Point(x+2,y+2);
                }else{
                    ai.add(new Point(x-2,y-2));return new Point(x-2,y-2);
                }
            }
            if(pr.contains(new Point(x+1,y-1))&&pr.contains(new Point(x-1,y+1))&&!ai.contains(new Point(x+2,y-2))&&!ai.contains(new Point(x-2,y+2))&&!pr.contains(new Point(x+2,y-2))&&!pr.contains(new Point(x-2,y+2))&&XY(x+2,y-2)==0&&XY(x-2,y+2)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y-2));return new Point(x+2,y-2);
                }else{
                    ai.add(new Point(x-2,y+2));return new Point(x-2,y+2);
                }
            }
        }

        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            if(pr.contains(new Point(x+1,y))&&!ai.contains(new Point(x-1,y))&&!pr.contains(new Point(x-1,y))&&pr.contains(new Point(x-2,y))&&XY(x-1,y)==0){
                ai.add(new Point(x-1,y));return new Point(x-1,y);
            }
            if(pr.contains(new Point(x-1,y))&&!ai.contains(new Point(x+1,y))&&!pr.contains(new Point(x+1,y))&&pr.contains(new Point(x+2,y))&&XY(x+1,y)==0){
                ai.add(new Point(x+1,y));return new Point(x+1,y);
            }
            if(pr.contains(new Point(x,y+1))&&!ai.contains(new Point(x,y-1))&&!pr.contains(new Point(x,y-1))&&pr.contains(new Point(x,y-2))&&XY(x,y-1)==0){
                ai.add(new Point(x,y-1));return new Point(x,y-1);
            }
            if(pr.contains(new Point(x,y-1))&&!ai.contains(new Point(x,y+1))&&!pr.contains(new Point(x,y+1))&&pr.contains(new Point(x,y+2))&&XY(x,y+1)==0){
                ai.add(new Point(x,y+1));return new Point(x,y+1);
            }
            if(pr.contains(new Point(x+1,y+1))&&!ai.contains(new Point(x-1,y-1))&&!pr.contains(new Point(x-1,y-1))&&pr.contains(new Point(x-2,y-2))&&XY(x-1,y-1)==0){
                ai.add(new Point(x-1,y-1));return new Point(x-1,y-1);
            }
            if(pr.contains(new Point(x-1,y-1))&&!ai.contains(new Point(x+1,y+1))&&!pr.contains(new Point(x+1,y+1))&&pr.contains(new Point(x+2,y+2))&&XY(x+1,y+1)==0){
                ai.add(new Point(x+1,y+1));return new Point(x+1,y+1);
            }
            if(pr.contains(new Point(x+1,y-1))&&!ai.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x-1,y+1))&&pr.contains(new Point(x-2,y+2))&&XY(x-1,y+1)==0){
                ai.add(new Point(x-1,y+1));return new Point(x-1,y+1);
            }
            if(pr.contains(new Point(x-1,y+1))&&!ai.contains(new Point(x+1,y-1))&&!pr.contains(new Point(x+1,y-1))&&pr.contains(new Point(x+2,y-2))&&XY(x+1,y-1)==0){
                ai.add(new Point(x+1,y-1));return new Point(x+1,y-1);
            }
        }

        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            if(pr.contains(new Point(x-1,y))&&!pr.contains(new Point(x+1,y))&&!ai.contains(new Point(x+1,y))&&pr.contains(new Point(x+2,y))&&pr.contains(new Point(x+3,y))&&XY(x-1,y)==0){
                ai.add(new Point(x+1,y));return new Point(x+1,y);
            }
            if(pr.contains(new Point(x,y-1))&&!pr.contains(new Point(x,y+1))&&!ai.contains(new Point(x,y+1))&&pr.contains(new Point(x,y+2))&&pr.contains(new Point(x,y+3))&&XY(x,y+1)==0){
                ai.add(new Point(x,y+1));return new Point(x,y+1);
            }
            if(pr.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x+1,y-1))&&!ai.contains(new Point(x+1,y-1))&&pr.contains(new Point(x+2,y-2))&&pr.contains(new Point(x+3,y-3))&&XY(x+1,y-1)==0){
                ai.add(new Point(x+1,y-1));return new Point(x+1,y-1);
            }
            if(pr.contains(new Point(x-1,y-1))&&!pr.contains(new Point(x+1,y+1))&&!ai.contains(new Point(x+1,y+1))&&pr.contains(new Point(x+2,y+2))&&pr.contains(new Point(x+3,y+3))&&XY(x+1,y+1)==0){
                ai.add(new Point(x+1,y+1));return new Point(x+1,y+1);
            }
        }
        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            if(pr.contains(new Point(x+1,y))&&pr.contains(new Point(x+2,y))&&pr.contains(new Point(x+3,y))){
                if(!pr.contains(new Point(x-1,y))&&!ai.contains(new Point(x-1,y))&&XY(x-1,y)==0){
                    ai.add(new Point(x-1,y));return new Point(x-1,y);
                }
                else if(!pr.contains(new Point(x+4,y))&&!ai.contains(new Point(x+4,y))&&XY(x+4,y)==0){
                    ai.add(new Point(x+4,y));return new Point(x+4,y);
                }
            }
            if(pr.contains(new Point(x-1,y))&&pr.contains(new Point(x-2,y))&&pr.contains(new Point(x-3,y))){
                if(!pr.contains(new Point(x+1,y))&&!ai.contains(new Point(x+1,y))&&XY(x+1,y)==0){
                    ai.add(new Point(x+1,y));return new Point(x+1,y);
                }
                else if(!pr.contains(new Point(x-4,y))&&!ai.contains(new Point(x-4,y))&&XY(x-4,y)==0){
                    ai.add(new Point(x-4,y));return new Point(x-4,y);
                }
            }
            if(pr.contains(new Point(x,y-1))&&pr.contains(new Point(x,y-2))&&pr.contains(new Point(x,y-3))){
                if(!pr.contains(new Point(x,y-4))&&!ai.contains(new Point(x,y-4))&&XY(x,y-4)==0){
                    ai.add(new Point(x,y-4));return new Point(x,y-4);
                }
                else if(!pr.contains(new Point(x,y+1))&&!ai.contains(new Point(x,y+1))&&XY(x,y+1)==0){
                    ai.add(new Point(x,y+1));return new Point(x,y+1);
                }
            }
            if(pr.contains(new Point(x,y+1))&&pr.contains(new Point(x,y+2))&&pr.contains(new Point(x,y+3))){
                if(!pr.contains(new Point(x,y+4))&&!ai.contains(new Point(x,y+4))&&XY(x,y+4)==0){
                    ai.add(new Point(x,y+4));return new Point(x,y+4);
                }
                else if(!pr.contains(new Point(x,y-1))&&!ai.contains(new Point(x,y-1))&&XY(x,y-1)==0){
                    ai.add(new Point(x,y-1));return new Point(x,y-1);
                }
            }
            if(pr.contains(new Point(x+1,y+1))&&pr.contains(new Point(x+2,y+2))&&pr.contains(new Point(x+3,y+3))){
                if(!pr.contains(new Point(x+4,y+4))&&!ai.contains(new Point(x+4,y+4))&&XY(x+4,y+4)==0){
                    ai.add(new Point(x+4,y+4));return new Point(x+4,y+4);
                }
                else if(!pr.contains(new Point(x-1,y-1))&&!ai.contains(new Point(x-1,y-1))&&XY(x-1,y-1)==0){
                    ai.add(new Point(x-1,y-1));return new Point(x-1,y-1);
                }
            }
            if(pr.contains(new Point(x-1,y-1))&&pr.contains(new Point(x-2,y-2))&&pr.contains(new Point(x-3,y-3))){
                if(!pr.contains(new Point(x-4,y-4))&&!ai.contains(new Point(x-4,y-4))&&XY(x-4,y-4)==0){
                    ai.add(new Point(x-4,y-4));return new Point(x-4,y-4);
                }
                else if(!pr.contains(new Point(x+1,y+1))&&!ai.contains(new Point(x+1,y+1))&&XY(x+1,y+1)==0){
                    ai.add(new Point(x+1,y+1));return new Point(x+1,y+1);
                }
            }
            if(pr.contains(new Point(x-1,y+1))&&pr.contains(new Point(x-2,y+2))&&pr.contains(new Point(x-3,y+3))){
                if(!pr.contains(new Point(x-4,y+4))&&!ai.contains(new Point(x-4,y+4))&&XY(x-4,y+4)==0){
                    ai.add(new Point(x-4,y+4));return new Point(x-4,y+4);
                }
                else if(!pr.contains(new Point(x+1,y-1))&&!ai.contains(new Point(x+1,y-1))&&XY(x+1,y-1)==0){
                    ai.add(new Point(x+1,y-1));return new Point(x+1,y-1);
                }
            }
            if(pr.contains(new Point(x+1,y-1))&&pr.contains(new Point(x+2,y-2))&&pr.contains(new Point(x+3,y-3))){
                if(!pr.contains(new Point(x+4,y-4))&&!ai.contains(new Point(x+4,y-4))&&XY(x+4,y-4)==0){
                    ai.add(new Point(x+4,y-4));return new Point(x+4,y-4);
                }
                else if(!pr.contains(new Point(x-1,y+1))&&!ai.contains(new Point(x-1,y+1))&&XY(x-1,y+1)==0){
                    ai.add(new Point(x-1,y+1));return new Point(x-1,y+1);
                }
            }
        }

        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            //对方两子
            if(pr.contains(new Point(x+1,y))&&!ai.contains(new Point(x+2,y))&&!ai.contains(new Point(x-1,y))&&!pr.contains(new Point(x+2,y))&&!pr.contains(new Point(x-1,y))&&XY(x+2,y)==0&&XY(x-1,y)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y));return new Point(x+2,y);
                }else{
                    ai.add(new Point(x-1,y));return new Point(x-1,y);
                }
            }
            if(pr.contains(new Point(x-1,y))&&!ai.contains(new Point(x-2,y))&&!ai.contains(new Point(x+1,y))&&!pr.contains(new Point(x-2,y))&&!pr.contains(new Point(x+1,y))&&XY(x-2,y)==0&&XY(x+1,y)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x-2,y));return new Point(x-2,y);
                }else{
                    ai.add(new Point(x+1,y));return new Point(x+1,y);
                }
            }
            if(pr.contains(new Point(x,y-1))&&!ai.contains(new Point(x,y-2))&&!ai.contains(new Point(x,y+1))&&!pr.contains(new Point(x,y-2))&&!pr.contains(new Point(x,y+1))&&XY(x,y-2)==0&&XY(x,y+1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x,y-2));return new Point(x,y-2);
                }else{
                    ai.add(new Point(x,y+1));return new Point(x,y+1);
                }
            }
            if(pr.contains(new Point(x,y+1))&&!ai.contains(new Point(x,y+2))&&!ai.contains(new Point(x,y-1))&&!pr.contains(new Point(x,y+2))&&!pr.contains(new Point(x,y-1))&&XY(x,y+2)==0&&XY(x,y-1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x,y+2));new Point(x,y+2);
                }else{
                    ai.add(new Point(x,y-1));return new Point(x,y-1);
                }
            }
            if(pr.contains(new Point(x+1,y+1))&&!ai.contains(new Point(x+2,y+2))&&!ai.contains(new Point(x-1,y-1))&&!pr.contains(new Point(x+2,y+2))&&!pr.contains(new Point(x-1,y-1))&&XY(x+2,y+2)==0&&XY(x-1,y-1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y+2));return new Point(x+2,y+2);
                }else{
                    ai.add(new Point(x-1,y-1));return new Point(x-1,y-1);
                }
            }
            if(pr.contains(new Point(x-1,y-1))&&!ai.contains(new Point(x-2,y-2))&&!ai.contains(new Point(x+1,y+1))&&!pr.contains(new Point(x-2,y-2))&&!pr.contains(new Point(x+1,y+1))&&XY(x-2,y-2)==0&&XY(x+1,y+1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x-2,y-2));return new Point(x-2,y-2);
                }else{
                    ai.add(new Point(x+1,y+1));return new Point(x+1,y+1);
                }
            }
            if(pr.contains(new Point(x+1,y-1))&&!ai.contains(new Point(x+2,y-2))&&!ai.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x+2,y-2))&&!pr.contains(new Point(x-1,y+1))&&XY(x+2,x-2)==0&&XY(x-1,y+1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x+2,y-2));return new Point(x+2,y-2);
                }else{
                    ai.add(new Point(x-1,y+1));return new Point(x-1,y+1);
                }
            }
            if(pr.contains(new Point(x-1,y+1))&&!ai.contains(new Point(x-2,y+2))&&!ai.contains(new Point(x+1,y-1))&&!pr.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x-2,y+2))&&XY(x-2,y+2)==0&&XY(x+1,y+1)==0){
                Random random=new Random();
                int len=random.nextInt(2);
                if(len==0){
                    ai.add(new Point(x-2,y+2));return new Point(x-2,y+2);
                }else{
                    ai.add(new Point(x+1,y-1));return new Point(x+1,y-1);
                }
            }
        }

        for(Point p:pr){
            //继续堵
            int x=p.x;
            int y=p.y;
            if(pr.contains(new Point(x+1,y))&&!ai.contains(new Point(x-1,y))&&!pr.contains(new Point(x-1,y))&&XY(x-1,y)==0){
                ai.add(new Point(x-1,y));return new Point(x-1,y);
            }
            if(pr.contains(new Point(x-1,y))&&!ai.contains(new Point(x+1,y))&&!pr.contains(new Point(x+1,y))&&XY(x+1,y)==0){
                ai.add(new Point(x+1,y));return new Point(x+1,y);
            }
            if(pr.contains(new Point(x,y-1))&&!ai.contains(new Point(x,y+1))&&!pr.contains(new Point(x,y+1))&&XY(x,y+1)==0){
                ai.add(new Point(x,y+1));return new Point(x,y+1);
            }
            if(pr.contains(new Point(x,y+1))&&!ai.contains(new Point(x,y-1))&&!pr.contains(new Point(x,y-1))&&XY(x,y-1)==0){
                ai.add(new Point(x,y-1));return new Point(x,y-1);
            }
            if(pr.contains(new Point(x+1,y+1))&&!ai.contains(new Point(x-1,y-1))&&!pr.contains(new Point(x-1,y-1))&&XY(x-1,y-1)==0){
                ai.add(new Point(x-1,y-1));return new Point(x-1,y-1);
            }
            if(pr.contains(new Point(x-1,y-1))&&!ai.contains(new Point(x+1,y+1))&&!pr.contains(new Point(x+1,y+1))&&XY(x+1,y+1)==0){
                ai.add(new Point(x+1,y+1));return new Point(x+1,y+1);
            }
            if(pr.contains(new Point(x-1,y+1))&&!ai.contains(new Point(x+1,y-1))&&!pr.contains(new Point(x+1,y-1))&&XY(x+1,y-1)==0){
                ai.add(new Point(x+1,y-1));return new Point(x+1,y-1);
            }
            if(pr.contains(new Point(x+1,y-1))&&!ai.contains(new Point(x-1,y+1))&&!pr.contains(new Point(x-1,y+1))&&XY(x-1,y+1)==0){
                ai.add(new Point(x-1,y+1));return new Point(x-1,y+1);
            }

        }

        for(Point p:ai){
            int x=p.x;
            int y=p.y;
            Random random=new Random();
            int len=random.nextInt(8);
            boolean add=false;
            for(int j=0;j<2;j++) {
                if (len==0&&!pr.contains((new Point(x - 1, y - 1))) && !ai.contains((new Point(x - 1, y - 1))) && XY(x - 1, y - 1) == 0) {
                    ai.add(new Point(x - 1, y - 1));
                    return new Point(x - 1, y - 1);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==1&&!pr.contains((new Point(x, y - 1))) && !ai.contains((new Point(x, y - 1))) && XY(x, y - 1) == 0) {

                    ai.add(new Point(x, y - 1));
                    return new Point(x, y - 1);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==2&&!pr.contains((new Point(x + 1, y - 1))) && !ai.contains((new Point(x + 1, y - 1))) && XY(x + 1, y - 1) == 0) {
                    ai.add(new Point(x + 1, y - 1));
                    return new Point(x + 1, y - 1);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==3&&!pr.contains((new Point(x - 1, y))) && !ai.contains((new Point(x - 1, y))) && XY(x - 1, y) == 0) {
                    ai.add(new Point(x - 1, y));
                    return new Point(x - 1, y);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==4&&!pr.contains((new Point(x + 1, y))) && !ai.contains((new Point(x + 1, y))) && XY(x + 1, y) == 0) {
                    ai.add(new Point(x + 1, y));
                    return new Point(x + 1, y);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==5&&!pr.contains((new Point(x - 1, y + 1))) && !ai.contains((new Point(x - 1, y + 1))) && XY(x - 1, y + 1) == 0) {
                    ai.add(new Point(x - 1, y + 1));
                    return new Point(x - 1, y + 1);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==6&&!pr.contains((new Point(x, y + 1))) && !ai.contains((new Point(x, y + 1))) && XY(x, y + 1) == 0) {
                    ai.add(new Point(x, y + 1));
                    return new Point(x, y + 1);
                }else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len==7&&!pr.contains((new Point(x + 1, y + 1))) && !ai.contains((new Point(x + 1, y + 1))) && XY(x + 1, y + 1) == 0) {
                    ai.add(new Point(x + 1, y + 1));
                    return new Point(x + 1, y + 1);
                }else if(len==7&&!add){add=true;len=0;}else if(add) len=0;
            }
        }
        for(Point p:pr){
            int x=p.x;
            int y=p.y;
            Random random=new Random();
            int len=random.nextInt(8);
            boolean add=false;
            for(i=0;i<2;i++) {
                if (len == 0 && !pr.contains((new Point(x - 1, y - 1))) && !ai.contains((new Point(x - 1, y - 1))) && XY(x - 1, y) == 0) {
                    ai.add(new Point(x-1 , y - 1));
                    return new Point(x-1 , y - 1);
                } else if(len==0&&!add){add=true;len++;}else if(add) len++;
                if (len == 1 && !pr.contains((new Point(x , y - 1))) && !ai.contains((new Point(x , y - 1))) && XY(x , y-1) == 0) {
                    ai.add(new Point(x,y - 1));
                    return new Point(x,y - 1);
                } else if(len==1&&!add){add=true;len++;}else if(add) len++;
                if (len == 2 && !pr.contains((new Point(x + 1, y - 1))) && !ai.contains((new Point(x + 1, y - 1))) && XY(x + 1, y - 1) == 0) {
                    ai.add(new Point(x + 1, y - 1));
                    return new Point(x + 1, y - 1);
                } else if(len==2&&!add){add=true;len++;}else if(add) len++;
                if (len == 3 && !pr.contains((new Point(x - 1, y))) && !ai.contains((new Point(x - 1, y))) && XY(x - 1, y) == 0) {
                    ai.add(new Point(x - 1, y));
                    return new Point(x - 1, y);
                } else if(len==3&&!add){add=true;len++;}else if(add) len++;
                if (len == 4 && !pr.contains((new Point(x + 1, y))) && !ai.contains((new Point(x + 1, y))) && XY(x + 1, y) == 0) {
                    ai.add(new Point(x + 1, y));
                    return new Point(x + 1, y);
                } else if(len==4&&!add){add=true;len++;}else if(add) len++;
                if (len == 5 && !pr.contains((new Point(x - 1, y + 1))) && !ai.contains((new Point(x - 1, y + 1))) && XY(x - 1, y - 1) == 0) {
                    ai.add(new Point(x - 1, y + 1));
                    return new Point(x - 1, y + 1);
                } else if(len==5&&!add){add=true;len++;}else if(add) len++;
                if (len == 6 && !pr.contains((new Point(x, y + 1))) && !ai.contains((new Point(x, y + 1))) && XY(x, y + 1) == 0) {
                    ai.add(new Point(x, y + 1));
                    return new Point(x, y + 1);
                } else if(len==6&&!add){add=true;len++;}else if(add) len++;
                if (len == 7 && !pr.contains((new Point(x + 1, y + 1))) && !ai.contains((new Point(x + 1, y + 1))) && XY(x + 1, y + 1) == 0) {
                    ai.add(new Point(x + 1, y + 1));
                    return new Point(x + 1, y + 1);
                } else if(len==7&&!add){add=true;len=0;}else if(add) len=0;
            }
        }
        return new Point(0,0);
    }
}
