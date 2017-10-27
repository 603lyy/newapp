package cn.yaheen.online.bean;

/**
 * Created by linjingsheng on 17/5/25.
 */

public class MsgBean {

    public int getColor() {
        return color;
    }

    public int getSeekBarVal() {
        return seekBarVal;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public void setSeekBarVal(int seekBarVal) {
        this.seekBarVal = seekBarVal;
    }

    private  int color;
    private  int seekBarVal;
}
