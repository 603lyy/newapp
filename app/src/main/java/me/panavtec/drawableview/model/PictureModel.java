package me.panavtec.drawableview.model;

import android.graphics.Bitmap;

/**
 * Created by linjingsheng on 17/4/17.
 */

public class PictureModel {

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void setSourse(Bitmap sourse) {
        this.sourse = sourse;
    }

    private  int width;//图片宽度
    private  int height;//图片高度
    private  int left; //图片左距离
    private  int top; //图片上距离
    private Bitmap sourse; //图片信息
    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getLeft() {
        return left;
    }

    public int getTop() {
        return top;
    }

    public Bitmap getSourse() {
        return sourse;
    }


}
