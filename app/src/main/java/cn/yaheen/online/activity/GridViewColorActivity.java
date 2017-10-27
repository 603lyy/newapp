package cn.yaheen.online.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SeekBar;

import com.google.gson.Gson;

import cn.yaheen.online.R;
import cn.yaheen.online.activity.online.OnlineMainActivity;
import cn.yaheen.online.app.*;
import cn.yaheen.online.bean.MsgBean;
import cn.yaheen.online.data.GridImageAdapter;
import cn.yaheen.online.utils.SysUtils;
import cn.yaheen.online.view.MSeekBar;
import cn.yaheen.online.view.SketchpadView;
import me.panavtec.drawableview.DrawableViewConfig;


/**
 * function:
 *
 * @author:
 */
public class GridViewColorActivity extends Activity {
    GridView my_gridview;
    private MSeekBar seekBar;
    private int curval = 2;
    //界面的高度
    private int height = 0;
    //界面的宽度
    private int width = 0;
    //界面宽度和高度之间的最小值
    private int minValue = 0;
    //判断是写字还是橡皮模式
    private boolean isEraserMode = false;
    private MsgBean msgBean = new MsgBean();
    GridImageAdapter myImageViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cn.yaheen.online.app.OnlineApp.getInstance().addActivity(this);
        setContentView(R.layout.mydialog);

        Intent intent = this.getIntent();
                /*取出Intent中附加的数据*/
        curval = intent.getIntExtra("curval", 2);
        isEraserMode = intent.getBooleanExtra("isEraser", false);
        String state = intent.getStringExtra("state");
        android.view.WindowManager.LayoutParams layoutParams = this.getWindow().getAttributes();

        //取宽高的最小值作为界面的宽高
        height = SysUtils.getsWindowHeight(GridViewColorActivity.this) * 2 / 3;
        width = SysUtils.getsWindowWidth(GridViewColorActivity.this) * 2 / 3;
        minValue = Math.min(height, width);
        layoutParams.width = minValue;
        layoutParams.height = minValue;
//        layoutParams.width = SysUtils.getsWindowWidth(GridViewColorActivity.this) / 2;
//        layoutParams.height = SysUtils.getsWindowHeight(GridViewColorActivity.this) / 3;
        this.getWindow().setAttributes(layoutParams);

        initSeekBar();
        if (!isEraserMode) {
            my_gridview = (GridView) findViewById(R.id.grid);
            /* 新建�?��自定义的 ImageAdapter*/
            myImageViewAdapter = new GridImageAdapter(GridViewColorActivity.this);
            /* �?GridView 对象设置�?�� ImageAdapter*/
            my_gridview.setAdapter(myImageViewAdapter);
            /* �?GridView 添加图片 Items 点击事件监听�?*/
            my_gridview.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0,
                                        View arg1, int arg2, long arg3) {
                    //Intent intent=GridViewDemoActivity.this.getIntent();
                    //Intent intent=new Intent(GridViewDemoActivity.this,PainterActivity.class);
                    int color = 0;
                    switch (arg2) {
                        case 0:
                            color = Color.RED;
                            break;
                        case 1:
                            color = Color.BLUE;
                            break;
                        case 2:
                            color = Color.BLACK;
                            break;
                        case 3:
                            color = 0xff458B00;
                            break;
                        case 4:
                            color = 0xff8B0000;
                            break;
                        case 5:
                            color = 0xff7CFC00;
                            break;
                        case 6:
                            color = 0xffFF00FF;
                            break;
                        case 7:
                            color = 0xffEE1289;
                            break;
                        case 8:
                            color = 0xffB23AEE;
                            break;
                        case 9:
                            color = 0xff00FFFF;
                            break;
                        case 10:
                            color = 0xff27408B;
                            break;
                        case 11:
                            color = 0xffFF8247;
                            break;
                        case 12:
                            color = 0xffFFFF00;
                            break;
                        case 13:
                            color = 0xff458B74;
                            break;
                        case 14:
                            color = 0xff8B7500;
                            break;
                        case 15:
                            color = Color.WHITE;
                            break;
                        default:
                    }

                    msgBean.setColor(color);
                    sendMsg(msgBean);
                    finish();
                    //设置当前的颜�?
                    //OnlineMainActivity.setColor(color);
                    //SketchpadView.setStrokeColor(color);

                }
            });
        }
    }

    private void initSeekBar() {
        seekBar = (MSeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        seekBar.setProgress((curval - 5) * 10); //设置画笔宽度
        //�������¼�����
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

//                int value = (seekBar.getProgress() / 40) * 5 + 5;
                int value = (seekBar.getProgress() / 10) + 5;
                msgBean.setSeekBarVal(value);
                if (isEraserMode) {
                    msgBean.setColor(Color.WHITE);
                } else {
                    msgBean.setColor(0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                sendMsg(msgBean);
            }
        });
    }


    private void sendMsg(MsgBean msgBean) {

        Gson gson = new Gson();
        String msg = gson.toJson(msgBean);
        Intent intt = new Intent();
        intt.setAction("cn.yaheen.color");
        intt.putExtra("msg", msg);
        GridViewColorActivity.this.sendBroadcast(intt); //发送广播更新界面

    }
}
