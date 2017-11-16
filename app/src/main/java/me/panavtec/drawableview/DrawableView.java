package me.panavtec.drawableview;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import me.panavtec.drawableview.draw.BitmapCanvas;
import me.panavtec.drawableview.draw.CanvasDrawer;
import me.panavtec.drawableview.draw.PathDrawer;
import me.panavtec.drawableview.draw.SerializablePath;
import me.panavtec.drawableview.gestures.creator.GestureCreator;
import me.panavtec.drawableview.gestures.creator.GestureCreatorListener;
import me.panavtec.drawableview.gestures.scale.GestureScaleListener;
import me.panavtec.drawableview.gestures.scale.GestureScaler;
import me.panavtec.drawableview.gestures.scale.ScalerListener;
import me.panavtec.drawableview.gestures.scroller.GestureScrollListener;
import me.panavtec.drawableview.gestures.scroller.GestureScroller;
import me.panavtec.drawableview.gestures.scroller.ScrollerListener;
import me.panavtec.drawableview.model.CanvasMode;
import me.panavtec.drawableview.model.PictureModel;

public class DrawableView extends View
        implements View.OnTouchListener, ScrollerListener, GestureCreatorListener, ScalerListener {

    public final ArrayList<SerializablePath> paths = new ArrayList<>();

    private GestureScroller gestureScroller;
    private GestureScaler gestureScaler;
    private BitmapCanvas bitmapCanvas;
    private GestureCreator gestureCreator;
    private int canvasHeight;
    private int canvasWidth;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    public PathDrawer pathDrawer;
    private CanvasDrawer canvasDrawer;
    private SerializablePath currentDrawingPath;
    private Bitmap creatBitmap;

    public boolean isOneFingerMode() {
        return OneFingerMode;
    }

    public void setOneFingerMode(boolean oneFingerMode) {
        OneFingerMode = oneFingerMode;
    }

    private boolean OneFingerMode = false;


    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        bitMode = true;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap bitmap = null;

    public void setCanvasMode(CanvasMode canvasMode) {
        this.canvasMode = canvasMode;
    }

    public CanvasMode getCanvasMode() {
        return canvasMode;
    }

    private CanvasMode canvasMode;
    private boolean bitMode = false;

    public DrawableView(Context context) {
        super(context);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableView(Context context, AttributeSet attrs,
                        int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        gestureScroller = new GestureScroller(this);
        bitmapCanvas = new BitmapCanvas();
        gestureDetector = new GestureDetector(getContext(), new GestureScrollListener(gestureScroller));
        gestureScaler = new GestureScaler(this);
        scaleGestureDetector =
                new ScaleGestureDetector(getContext(), new GestureScaleListener(gestureScaler));
        gestureCreator = new GestureCreator(this);
        pathDrawer = new PathDrawer();
        canvasDrawer = new CanvasDrawer();
        setOnTouchListener(this);
    }

    public void setConfig(DrawableViewConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Paint configuration cannot be null");
        }
        canvasWidth = config.getCanvasWidth();
        canvasHeight = config.getCanvasHeight();
        gestureCreator.setConfig(config);
        gestureScaler.setZooms(config.getMinZoom(), config.getMaxZoom());
        gestureScroller.setCanvasBounds(canvasWidth, canvasHeight);
        canvasDrawer.setConfig(config);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        gestureScroller.setViewBounds(w, h);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        gestureCreator.onTouchEvent(event);
        invalidate();
        return true;
    }

    public void undo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public void initff() {
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        gestureScroller.OneFingerMode = OneFingerMode;
        canvas.drawRGB(255, 255, 255);            // 绘制背景罗盘
        canvasDrawer.onDraw(canvas);
        if (bitMode) {
            bitmapCanvas.onDraw(canvas, bitmap);//Edit by xszyou on 20170604: 绘制截图或拍照.
            //bitMode=false;
        }
        pathDrawer.onDraw(canvas, currentDrawingPath, paths);
    }

    public void clear() {
        paths.clear();
        bitmap = null;
        bitMode = false;
        invalidate();
    }

    public void clearPaths() {
        paths.clear();
        invalidate();
    }

    public Bitmap obtainBitmap(Bitmap createdBitmap) {
        return pathDrawer.obtainBitmap(createdBitmap, paths);
    }

    public Bitmap obtainBitmapNopath(Bitmap createdBitmap) {
        return pathDrawer.obtainBitmap(createdBitmap);
    }

    public Bitmap obtainBitmap() {
        return obtainBitmap(Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888));
    }

    //Edit by xszyou on 20170617:获取最终效果图？
    public Bitmap obtainBitmapByP() {

        PictureModel pictureModel = null;
        if (bitmap != null) {
            pictureModel = new PictureModel();
            pictureModel.setTop(0);
            pictureModel.setLeft(0);
            pictureModel.setSourse(bitmap);
        }

        if (creatBitmap == null) {
            creatBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        }

        return pathDrawer.obtainBitmapByP(creatBitmap, paths, pictureModel);
    }


    @Override
    protected Parcelable onSaveInstanceState() {
        DrawableViewSaveState state = new DrawableViewSaveState(super.onSaveInstanceState());
        state.setPaths(paths);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof DrawableViewSaveState)) {
            super.onRestoreInstanceState(state);
        } else {
            DrawableViewSaveState ss = (DrawableViewSaveState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            paths.addAll(ss.getPaths());
        }
    }

    @Override
    public void onViewPortChange(RectF currentViewport) {
        gestureCreator.onViewPortChange(currentViewport);
        canvasDrawer.onViewPortChange(currentViewport);
    }

    @Override
    public void onCanvasChanged(RectF canvasRect) {
        gestureCreator.onCanvasChanged(canvasRect);
        canvasDrawer.onCanvasChanged(canvasRect);
    }

    @Override
    public void onGestureCreated(SerializablePath serializablePath) {
        if (!OneFingerMode) {
            paths.add(serializablePath);
        }
        gestureScroller.OneFingerMode = OneFingerMode;

    }

    @Override
    public void onCurrentGestureChanged(SerializablePath currentDrawingPath) {
        this.currentDrawingPath = currentDrawingPath;
    }

    @Override
    public void onScaleChange(float scaleFactor) {
        gestureScroller.onScaleChange(scaleFactor);
        gestureCreator.onScaleChange(scaleFactor);
        canvasDrawer.onScaleChange(scaleFactor);
    }


}
