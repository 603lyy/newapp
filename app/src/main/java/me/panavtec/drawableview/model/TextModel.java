package me.panavtec.drawableview.model;

import android.graphics.Bitmap;

/**
 * Created by linjingsheng on 17/4/17.
 */

public class TextModel {

    public void setWidth(int width) {
        this.width = width;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setTop(int top) {
        this.top = top;
    }


    private  int width;
    private  int left;
    private  int top;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    private String text; //图片信息
    public int getWidth() {
        return width;
    }


    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

}
