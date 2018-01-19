package cn.yaheen.online.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import cn.yaheen.online.R;

public class PopupMenu extends PopupWindow implements OnClickListener {

    private Activity activity;
    private View popView;

    private View save;
    private View upload;
    private View delete;
    private View finish;
    private View cut;
    private View langscape;
    private View clear;
    private TextView colorText; //颜色对应的文字
    private TextView toolsText; //橡皮对应的文字

    private Boolean isHeng = false;

    private OnItemClickListener onItemClickListener;

    /**
     * @author ywl5320 枚举，用于区分选择了哪个选项
     */
    public enum MENUITEM {
        ITEM1, ITEM2, ITEM3, ITEM5, ITEM6, ITEM7, ITEM8, ITEM9, ITEM10
    }

    public enum TYPE {
        ONLINE, OFFLINE, GOSCHOOL,
    }

    public PopupMenu(Activity activity, TYPE type, Boolean isheng) {
        super(activity);
        this.isHeng = isheng;
        this.activity = activity;
        LayoutInflater inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (type == TYPE.ONLINE) {
            popView = inflater.inflate(R.layout.popup_menu, null);// 加载菜单布局文件

        } else if (type == TYPE.OFFLINE) {
            popView = inflater.inflate(R.layout.popup_menu_o, null);// 加载菜单布局文件
        }

        this.setContentView(popView);// 把布局文件添加到popupwindow中
        if (type == TYPE.ONLINE) {
            this.setWidth(dip2px(activity, 300));// 设置菜单的宽度（需要和菜单于右边距的距离搭配，可以自己调到合适的位置）
        } else if (type == TYPE.OFFLINE) {
            this.setWidth(dip2px(activity, 300));// 设置菜单的宽度（需要和菜单于右边距的距离搭配，可以自己调到合适的位置）
        }

        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);// 获取焦点
        this.setTouchable(true); // 设置PopupWindow可触摸
        this.setOutsideTouchable(true); // 设置非PopupWindow区域可触摸
        ColorDrawable dw = new ColorDrawable(0x00000000);
        this.setBackgroundDrawable(dw);

        // 获取选项卡
        save = popView.findViewById(R.id.save);
        upload = popView.findViewById(R.id.upload);
        delete = popView.findViewById(R.id.delete);


        if (type == TYPE.ONLINE) {
            finish = popView.findViewById(R.id.finish);
            finish.setOnClickListener(this);
        }
        toolsText = (TextView) popView.findViewById(R.id.toolstext);
        langscape = popView.findViewById(R.id.langscape);
        clear = popView.findViewById(R.id.clear);
        cut = popView.findViewById(R.id.cut);

        // 添加监听
        cut.setOnClickListener(this);
        save.setOnClickListener(this);
        clear.setOnClickListener(this);
        upload.setOnClickListener(this);
        delete.setOnClickListener(this);
        langscape.setOnClickListener(this);


        if (isHeng) {
            ((ImageView) popView.findViewById(R.id.langscapeBtn)).setImageResource(R.drawable.portrait_btn);
            ((TextView) popView.findViewById(R.id.langscapeTxt)).setText("竖屏");
        } else {
            ((ImageView) popView.findViewById(R.id.langscapeBtn)).setImageResource(R.drawable.landscape_btn);
            ((TextView) popView.findViewById(R.id.langscapeTxt)).setText("横屏");
        }


    }


    /**
     * 设置显示的位置
     *
     * @param resourId 这里的x,y值自己调整可以
     */
    public void showLocation(int resourId) {
        showAsDropDown(activity.findViewById(resourId), dip2px(activity, 0),
                dip2px(activity, -8));
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        MENUITEM menuitem = null;
        switch (v.getId()) {

            case R.id.save:
                menuitem = MENUITEM.ITEM1;
                break;
            case R.id.upload:
                menuitem = MENUITEM.ITEM2;
                break;
            case R.id.delete:
                menuitem = MENUITEM.ITEM3;
                break;


            case R.id.finish:
                menuitem = MENUITEM.ITEM5;
                break;

            case R.id.cut:
                menuitem = MENUITEM.ITEM6;
                break;
            case R.id.langscape:
                menuitem = MENUITEM.ITEM7;

                break;
            case R.id.prestep:
                menuitem = MENUITEM.ITEM8;
                break;
            case R.id.clear:
                menuitem = MENUITEM.ITEM10;
                break;
        }

        if (onItemClickListener != null) {
            onItemClickListener.onClick(menuitem);
        }

        dismiss();

        //dismiss();
    }

    // dip转换为px
    public int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    // 点击监听接口
    public interface OnItemClickListener {
        public void onClick(MENUITEM item);
    }

    // 设置监听
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


    //设置橡皮文字
    public void setEraserText(String text) {
        if (toolsText == null) {
            toolsText = (TextView) popView.findViewById(R.id.toolstext);

        }
        toolsText.setText(text);

    }

    public void setHeng(Boolean heng) {
        isHeng = heng;
    }
}
