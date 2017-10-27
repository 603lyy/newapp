package me.panavtec.drawableview.draw;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import java.util.List;

import cn.yaheen.online.utils.ImgUtil;
import me.panavtec.drawableview.DrawableViewConfig;
import me.panavtec.drawableview.model.PictureModel;

public class PathDrawer {

  private Paint gesturePaint;


  public PathDrawer() {
    initGesturePaint();
  }

  public void onDraw(Canvas canvas, SerializablePath currentDrawingPath, List<SerializablePath> paths) {
    drawGestures(canvas, paths);
    if (currentDrawingPath != null) {
      drawGesture(canvas, currentDrawingPath);
    }
  }


  public void onDraw(Canvas canvas,Bitmap bitmap) {
    canvas.drawBitmap(bitmap,0,100,gesturePaint);
  }

  public void drawGestures(Canvas canvas, List<SerializablePath> paths) {
    for (SerializablePath path : paths) {
      drawGesture(canvas, path);
    }
  }

  public Bitmap obtainBitmap(Bitmap createdBitmap, List<SerializablePath> paths) {
    Canvas composeCanvas = new Canvas(createdBitmap);
    drawGestures(composeCanvas, paths);
    return createdBitmap;
  }




  /**
   * 带图片截图
   * @param createdBitmap
   * @param paths
   * @param pictureModel
   * @return
   */
  public Bitmap obtainBitmapByP(Bitmap createdBitmap, List<SerializablePath> paths, PictureModel pictureModel) {

    Canvas composeCanvas = new Canvas(createdBitmap);
    if (pictureModel!=null){

      float height=pictureModel.getSourse().getHeight()*(1320f/pictureModel.getSourse().getWidth());
      Bitmap thum = ImgUtil.big(pictureModel.getSourse(),1320,height);
      composeCanvas.drawBitmap(thum,pictureModel.getLeft(),pictureModel.getTop(),gesturePaint);

    }
    drawGestures(composeCanvas, paths);

    return createdBitmap;
  }



  public Bitmap obtainBitmap(Bitmap createdBitmap) {
    Canvas composeCanvas = new Canvas(createdBitmap);
    drawbg(composeCanvas,createdBitmap);
    return createdBitmap;
  }
  private void drawGesture(Canvas canvas, SerializablePath path) {
    gesturePaint.setStrokeWidth(path.getWidth());

    gesturePaint.setColor(path.getColor());

    if (path.getColor()==Color.WHITE){
               System.out.print(""+path.getColor());

    }else if (path.getColor()==Color.BLACK){
      System.out.print(""+path.getColor());

    }
    canvas.drawPath(path, gesturePaint);


  }


  private void drawbg(Canvas canvas, Bitmap bitmap) {
   ;
    canvas.drawBitmap(bitmap,0,0,gesturePaint);
    canvas.save();
    canvas.restore();
  }
  private void initGesturePaint() {
    gesturePaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
    gesturePaint.setStyle(Paint.Style.STROKE);
    gesturePaint.setStrokeJoin(Paint.Join.ROUND);
    gesturePaint.setStrokeCap(Paint.Cap.ROUND);
  }


}
