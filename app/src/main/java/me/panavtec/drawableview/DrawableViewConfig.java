package me.panavtec.drawableview;

import java.io.Serializable;

public class DrawableViewConfig implements Serializable {

    private float strokeWidth;
    private int strokeColor;
    private int canvasWidth;
    private int canvasHeight;
    private float minZoom;
    private float maxZoom;
    private boolean showCanvasBounds;
    private int textStrokeWidth;
    private int textStrokeColor;

    public boolean isTextMode() {
        return textMode;
    }

    public void setTextMode(boolean textMode) {
        this.textMode = textMode;
    }

    private boolean textMode; //是否为文字模式

    public void setEraserMode(boolean eraserMode) {
        this.eraserMode = eraserMode;
    }

    public boolean isEraserMode() {
        return eraserMode;
    }

    private boolean eraserMode;//是否橡皮擦模式

    public int getTextStrokeWidth() {
        return textStrokeWidth;
    }

    public int getTextStrokeColor() {
        return textStrokeColor;
    }

    public void setTextStrokeWidth(int textStrokeWidth) {
        this.textStrokeWidth = textStrokeWidth;
    }

    public void setTextStrokeColor(int textStrokeColor) {
        this.textStrokeColor = textStrokeColor;
    }


    public float getMaxZoom() {
        return maxZoom;
    }

    public void setMaxZoom(float maxZoom) {
        this.maxZoom = maxZoom;
    }

    public float getMinZoom() {
        return minZoom;
    }

    public void setMinZoom(float minZoom) {
        this.minZoom = minZoom;
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public float getStrokeWidth() {
        if (isEraserMode()) {
            return strokeWidth * 3;
        } else {
            return strokeWidth;
        }
    }

    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
    }

    public boolean isShowCanvasBounds() {
        return showCanvasBounds;
    }

    public void setShowCanvasBounds(boolean showCanvasBounds) {
        this.showCanvasBounds = showCanvasBounds;
    }
}
