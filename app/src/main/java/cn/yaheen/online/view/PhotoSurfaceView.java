package cn.yaheen.online.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by linjingsheng on 17/3/13.
 */

public class PhotoSurfaceView extends SurfaceView implements Runnable,SurfaceHolder.Callback{

    private Thread gameViewThread;
    private SurfaceHolder surfaceHolder;
    private Paint paint;
    private boolean runFlag=true;
    public static int screen_width,screen_height;
    protected Resources resources;
    private Canvas canvas;
    public static int cn=1;

    public PhotoSurfaceView(Context context) {
        super(context);
    }


    public PhotoSurfaceView(Context context, AttributeSet attrs){
        super(context,attrs);

    }



    private void init(){
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        resources=getResources();

        paint=new Paint();
        paint.setAntiAlias(true);


        //  setFocusable(true);
        //  setFocusableInTouchMode(true);
        //  setClickable(true);
        setKeepScreenOn(true);
    }

    public void draw(Canvas canvas, Paint paint){
        canvas.drawColor(Color.BLACK);

    }


    @Override
    public void run() {
        while(runFlag){
            try{
                canvas=surfaceHolder.lockCanvas();
                long startTime= System.currentTimeMillis();
                canvas.drawColor(Color.BLACK);
                draw(canvas,paint);
                long endTime= System.currentTimeMillis();
                if(50>(endTime-startTime)){
                    Thread.sleep(50-(endTime-startTime));
                }
            }catch (Exception e) {//Log.e("Error", "刷新屏幕出错！"+e);
            }finally{
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        screen_width=getWidth();
        screen_height=getHeight();
        runFlag=true;
        gameViewThread=new Thread(this);
        gameViewThread.start();

    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {


    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

        runFlag=false;
    }

}
