package cn.yaheen.online.activity.offline;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.List;
import java.util.UUID;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.Contral.PlygonCtl;
import cn.yaheen.online.R;
import cn.yaheen.online.activity.GridViewColorActivity;
import cn.yaheen.online.activity.SketchpadMainActivity;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.ImgUtil;
import cn.yaheen.online.view.SketchpadView;

public class OffMainActivity2 extends Activity implements  View.OnClickListener{



    private Button m_undo=null;
    private Button m_redo=null;
    private Button m_eraser=null;
    private Button m_new=null;
    private Button m_pen=null;
    private Button m_save=null;
    private Spinner m_open=null;
    private Button upload=null;
    private SeekBar seekBar;
    private static boolean plygon_Click = false;
    private Boolean mCurrentOrientation=true;
    private SketchpadView m_view;
    private  boolean isone=true; //防止select第一次加载
    private UploadDAO uploadDAO=null;

    private LinearLayout linearLayout=null;
    private  ImageView pic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_off_main2);

        linearLayout=(LinearLayout)this.findViewById(R.id.prentwidget);

        m_view=(SketchpadView)this.findViewById(R.id.SketchadView);

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View  childcanvs=inflater.inflate(R.layout.sketchpad_view_item, null);
        m_pen=(Button)this.findViewById(R.id.buttonpen_ID);
        m_undo=(Button)this.findViewById(R.id.buttonundo_ID);
        m_redo=(Button)this.findViewById(R.id.buttonredo_ID);
        m_eraser=(Button)this.findViewById(R.id.buttoneraser_ID);

        m_save=(Button)this.findViewById(R.id.buttonsave_ID);
        m_new=(Button)this.findViewById(R.id.buttonnew_ID);

//        try {
//            linearLayout.addView(childcanvs);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
        m_pen.setOnClickListener(this);
        m_undo.setOnClickListener(this);
        m_redo.setOnClickListener(this);
        m_eraser.setOnClickListener(this);

        if (uploadDAO==null){
            uploadDAO=new UploadDAO();
        }


        seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);
        //�������¼�����
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                SketchpadView.setStrokeSize((seekBar.getProgress()/40)*5+5, SketchpadView.STROKE_PEN);
                SketchpadView.setStrokeSize((seekBar.getProgress()/40)*5+5, SketchpadView.STROKE_ERASER);
                Log.v("progress1",(seekBar.getProgress())+"");
                Log.v("progress2",((seekBar.getProgress()/20)+3)+"");
            }
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        initPIc();
    }



    private  void initPIc(){

        pic=(ImageView)findViewById(R.id.imageView7);
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        if (bundle!=null){
            String picurl = bundle.getString("picurl");

            if (picurl!=null&&!"".equals(picurl)){

                Bitmap bitmap=BitmapUtil.loadBitmapFromSDCard(picurl);
                pic.setImageBitmap(bitmap);
            }

        }

    }



    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.buttonpen_ID:
                OnPenClick(v);
                break;
            case R.id.buttonundo_ID:
                OnUndoClick(v);
                break;
            case R.id.buttoneraser_ID:
                OnEraserClick(v);
                break;
            case R.id.buttonredo_ID:
                onRedoClick(v);
                break;

            case R.id.buttonsave_ID:
                OnSaveClick(v);
                break;
            case  R.id.buttonnew_ID:
                OnNewClick(v);
                break;

            default:
                break;
        }
    }




    private void OnLineClick(View v) {
        m_view.setStrokeType(m_view.STROKE_LINE);
    }
    private void OnOvalClick(View v) {
        m_view.setStrokeType(m_view.STROKE_OVAL);
    }
    private void OnCycleClick(View v) {
        m_view.setStrokeType(m_view.STROKE_CIRCLE);
    }
    private void OnRectClick(View v) {
        m_view.setStrokeType(m_view.STROKE_RECT);
    }
    private void OnPlygonClick(View v) {
        m_view.setStrokeType(m_view.STROKE_PLYGON);
        SketchpadMainActivity.setPlygon_Click(true);
    }
    private void OnEraserClick(View v) {
        //������Ƥ��������
        m_view.setStrokeType(m_view.STROKE_ERASER);
        //m_pen.setEnabled(true);
        //m_eraser.setEnabled(false);
    }
    private void onRedoClick(View v) {
        m_view.redo();//��Ӧredo�¼�
    }
    private void OnUndoClick(View v) {
        //��Ӧundo�¼�
        m_view.undo();
    }
    private void OnPenClick(View v) {
        m_view.setStrokeType(m_view.STROKE_PEN);//���û��ʵ�����
        //m_pen.setEnabled(false);
        //m_eraser.setEnabled(true);
    }
    private void OnColorClick(View v){
        //��ת��GridViewDemoActivity
        Intent intent=new Intent(OffMainActivity2.this,GridViewColorActivity.class);
        OffMainActivity2.this.startActivity(intent);
    }

    //	--------------------------ʷ����----------------------------------
    //�ж��Ƿ����˻��ƶ���ΰ�ť
    public static boolean isPlygon_Click() {
        return plygon_Click;
    }

    public static void setPlygon_Click(boolean plygon_Click) {
        OffMainActivity2.plygon_Click = plygon_Click;
        //���ö���α���Ϊ0  --ʷ����   2011-7-28---
        PlygonCtl.setCountLine(0);
    }



    private void OnSaveClick(View v) {

        SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);

        UploadModel uploadM=uploadDAO.findUploadByPageAndUID(1,"ui1d");
        if (uploadM!=null&&uploadM.getStatus()==1){
            Toast.makeText(OffMainActivity2.this,"该课程已上传，不可再做更改", Toast.LENGTH_SHORT).show();
        }else {
            SharedPreferences.Editor editor = sp.edit();

            editor.putBoolean("newpage", true);
            editor.commit();
            //   startActivityForResult(new Intent(this, SaveGridViewActivity.class),REQUEST_TYPE_B);
            try {
                String filename = null;
                File sdcarddir = android.os.Environment.getExternalStorageDirectory();

                filename = sdcarddir.getPath() + "/canvas/"+ UUID.randomUUID().toString() + ".jpg";



                // Bitmap bmp = m_view.getCanvasSnapshot();
                Bitmap bmp = m_view.getCanvasSnapshot();
                if (null != bmp)
                {
                    BitmapUtil.saveBitmapToSDCard(bmp, filename);
                }





                String mFileName = UUID.randomUUID().toString() + ".jpg";

                String strDir = sdcarddir.getPath() + "/mixpic/";
                File file = new File(strDir);
                if (!file.exists())
                {
                    file.mkdirs();
                }
                String mixfileName=strDir+mFileName;





                UploadModel uploadmdel=new UploadModel();


                if (uploadM!=null){
                    Bitmap mixpic=null;
                    uploadmdel.setId(uploadM.getId());
                    Bitmap bg= BitmapUtil.loadBitmapFromSDCard(uploadM.getBigpic());
                    if (bg!=null){
                        mixpic= ImgUtil.toConformBitmap(bg,bmp);
                    }else {
                        mixpic=bmp;
                    }
                    if (null != mixpic)
                    {
                        BitmapUtil.saveBitmapToSDCard(mixpic, mixfileName);
                    }



                }
                uploadmdel.setMixpic(mixfileName);


                uploadmdel.setCanvaspic(filename);
                uploadmdel.setStatus(0);

               // uploadmdel.setPage(page);
                uploadDAO.update(uploadmdel);
                List<UploadModel> list=uploadDAO.findAll();

                Toast.makeText(OffMainActivity2.this,"保存成功", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                Toast.makeText(OffMainActivity2.this,"保存失败", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }


    }



    private void OnNewClick(View v) {





    }



}
