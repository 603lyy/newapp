package me.panavtec.drawableview.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import cn.yaheen.online.utils.Constant;

/**
 * Created by linjingsheng on 17/4/12.
 */

public class BitmapCanvas {


    private Paint gesturePaint;

    public BitmapCanvas() {
        initGesturePaint();
    }

    /**
     * Edit by xszyou on 20170604:把图片调整大小再写入画板。
     * @param canvas
     * @param bitmap
     */
    public void onDraw(Canvas canvas, Bitmap bitmap) {

     //   canvas.drawBitmap(bitmap,10,100,gesturePaint);
        Log.d("test","原图大小：" + bitmap.getWidth() + "*" + bitmap.getHeight());
        Constant constant = Constant.createConstant(null);
        Rect rSource = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        Rect rDest = null;
        if (constant.isHeng()){
            rDest = new Rect(0, 0, 1320, (int)(bitmap.getHeight() * (1320f/bitmap.getWidth())));
        }else {
            rDest =  new Rect(0, 0, 1320, (int)(bitmap.getHeight() * (1320f/bitmap.getWidth())));
        }

        canvas.drawBitmap(bitmap, rSource, rDest, gesturePaint);
    }

    private void initGesturePaint() {
        gesturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
