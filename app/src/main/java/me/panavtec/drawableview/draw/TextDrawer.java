package me.panavtec.drawableview.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import me.panavtec.drawableview.model.TextModel;

/**
 * Created by linjingsheng on 17/4/17.
 */

public class TextDrawer {

    private Paint gesturePaint;

    public TextDrawer() {
        initGesturePaint();
    }

    public void onDraw(Canvas canvas, TextModel textModel) {
         int left=0,top=0;
         if (0!=textModel.getLeft()){
             left=textModel.getLeft();
         }
         if (0!=textModel.getTop()){
             top=textModel.getTop();
         }

        canvas.drawText(textModel.getText(),left,top,gesturePaint);
    }

    private void initGesturePaint() {
        gesturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        gesturePaint.setStyle(Paint.Style.STROKE);
        gesturePaint.setStrokeJoin(Paint.Join.ROUND);
        gesturePaint.setStrokeCap(Paint.Cap.ROUND);
    }
}
