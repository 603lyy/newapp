package cn.yaheen.online.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.Contral.PlygonCtl;
import cn.yaheen.online.R;
import cn.yaheen.online.bean.ResponseEntity;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.ImgUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.WeiboDialogUtils;
import cn.yaheen.online.utils.XHttpUtils;
import cn.yaheen.online.view.CommonProgressDialog;
import cn.yaheen.online.view.CustomDialog;
import cn.yaheen.online.view.SketchpadView;


public class SketchpadMainActivity extends Activity implements  View.OnClickListener,SurfaceHolder.Callback{
    /** Called when the activity is first created. */
	private Button m_undo=null;
	private Button m_redo=null;
	private Button m_eraser=null;

	private Button m_new=null;
	private Button m_pen=null;
	private Button m_save=null;
	private Spinner m_open=null;
	private Button upload=null;

	private SeekBar seekBar;
	public static int i =0;
	public static int size =1;
	CommonProgressDialog mDialog;

	private  boolean ispaizhao=false;

	 List<UploadModel> list=null;
	//记录用户首次点击返回键的时间
	private long firstTime=0;

	/**
	 * 拍照
	 */
	private Button mTakePhoto;
	private SurfaceView mSurfaceView;
	private Camera mCamera;
	private String mFileName;
	private OrientationEventListener mOrEventListener; // �豸���������
	private Boolean mCurrentOrientation=true;


	private TextView courseName=null;
	private SketchpadView m_view;
	private Dialog mWeiboDialog=null;
//	----------------------ʷ���� ------------------------------
	private static boolean plygon_Click = false; 
	private static boolean save_Click = false; 	
// -----------------------------------------------------------		
	private static final int REQUEST_TYPE_A = 1;
	private static final int REQUEST_TYPE_B = 2;
	UploadDAO uploadDAO=null;
	private ArrayAdapter<String> arr_adapter;
	private  boolean isone=true; //防止select第一次加载

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case 1:
					WeiboDialogUtils.closeDialog(mWeiboDialog);
					break;
			}
		}
	};
	  
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

//		mTakePhoto = (Button) findViewById(R.id.take_photo);
//		mTakePhoto.setOnClickListener(this); // ���հ�ť������

		startOrientationChangeListener(); // �����豸���������
		mSurfaceView = (SurfaceView) findViewById(R.id.surfaceView);
		SurfaceHolder holder = mSurfaceView.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		holder.addCallback(this); // �ص��ӿ�
		mSurfaceView.setOnClickListener(this);

		SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
		String course = sp.getString("courseName", "");
		uploadDAO=new UploadDAO();

		courseName=(TextView)findViewById(R.id.course);
		courseName.setText(course);
		m_view=null;
        m_view=(SketchpadView)this.findViewById(R.id.SketchadView);

		m_view.clearAllStrokes();
        m_undo=(Button)this.findViewById(R.id.buttonundo_ID);
        m_redo=(Button)this.findViewById(R.id.buttonredo_ID);
        m_eraser=(Button)this.findViewById(R.id.buttoneraser_ID);
		upload=(Button)this.findViewById(R.id.upload);
        m_pen=(Button)this.findViewById(R.id.buttonpen_ID);
        m_save=(Button)this.findViewById(R.id.buttonsave_ID);

        m_undo.setOnClickListener(this);
        m_redo.setOnClickListener(this);
        m_eraser.setOnClickListener(this);
		m_new=(Button)this.findViewById(R.id.buttonnew_ID);

        m_new.setOnClickListener(this);
        m_pen.setOnClickListener(this);
        m_save.setOnClickListener(this);
		upload.setOnClickListener(this);
        seekBar=(SeekBar) findViewById(R.id.seekBar);
        seekBar.setMax(100);




        //�������¼�����
        seekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
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


		m_open=(Spinner)findViewById(R.id.buttonopen_ID);
		loadPage();
//		m_open.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				loadPage();
//			}
//		});
	}


	private  void loadPage(){
		isone=true;
		SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
		String uuid = sp.getString("uid", null);
		list= uploadDAO.findByStatusAndUID(0,uuid);

		List<String> data_list = new ArrayList<String>();
		for (int i=0;i<list.size();i++){
			data_list.add(list.get(i)!=null?list.get(i).getPage()+"":"");
		}



		//适配器
		arr_adapter= new ArrayAdapter<String>(SketchpadMainActivity.this, android.R.layout.simple_spinner_item, data_list);
		//设置样式
		arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//加载适配器
		m_open.setAdapter(arr_adapter);
		if (data_list.size()!=0){
			m_open.setSelection(data_list.size()-1,true);
		}


		m_open.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

				if (!isone){
					try {


						UploadModel uploadModel=list.get(position);
						m_view.setBackground(null);
						m_view.clearAllStrokes();
						if (uploadModel!=null){
							Bitmap bg= BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());
							Bitmap canvas= BitmapUtil.loadBitmapFromSDCard(uploadModel.getCanvaspic());

							if (bg!=null){
								Drawable drawable=new BitmapDrawable(bg);
								mSurfaceView.setBackground(drawable);
							}else{
								mSurfaceView.setBackgroundResource(R.drawable.error);

							}

							if (canvas!=null){
								Drawable drawable=new BitmapDrawable(canvas);
								m_view.setBackground(drawable);
							}
						}

//                            m_open.setText("页数选择【"+uploadModel.getPage()+"】");

						//m_view.setForeBitmap(bmp);
						//m_view.setBkBitmap(bmp);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else {
					isone=false;
				}

			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});
	}





    /*���մ򿪶Ի���ͱ���Ի���Activity���ص�ֵ�����򿪺ͱ���ͼƬ*/
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
//		switch (requestCode) {
//		case 1:
//			if (resultCode == RESULT_OK) {
//				try {
//
//					int id = data.getIntExtra("bmp",0);
//
//					if (id!=0){
//					   UploadModel uploadModel=uploadDAO.findById(id);
//						Bitmap bg= BitmapUtil.loadBitmapFromSDCard(uploadModel.getCamerapic());
//						Bitmap canvas= BitmapUtil.loadBitmapFromSDCard(uploadModel.getCanvaspic());
//
//						if (bg!=null){
//							Drawable drawable=new BitmapDrawable(bg);
//							mSurfaceView.setBackground(drawable);
//						}else{
//							mSurfaceView.setBackgroundResource(R.drawable.error);
//
//						}
//
//						if (canvas!=null){
//							Drawable drawable=new BitmapDrawable(canvas);
//							m_view.setBackground(drawable);
//						}
//
//
//
//					}
//					//m_view.setForeBitmap(bmp);
//					//m_view.setBkBitmap(bmp);
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			break;
//		case 2:
//			if (resultCode == RESULT_OK) {
//				try {
//					String filename = null;
//					Bundle bundle = data.getExtras();
//					filename = bundle.getString("filePath");
//
//
//
//			       // Bitmap bmp = m_view.getCanvasSnapshot();
//					Bitmap bmp = m_view.getCanvasSnapshot();
//			        if (null != bmp)
//			        {
//			            BitmapUtil.saveBitmapToSDCard(bmp, filename);
//			        }
//					SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
//					String uuid = sp.getString("uid", null);
//					int page=sp.getInt("page",1);
//
//
//					mFileName = UUID.randomUUID().toString() + ".jpg";
//
//					File sdcarddir = android.os.Environment.getExternalStorageDirectory();
//					String strDir = sdcarddir.getPath() + "/mixpic/";
//					File file = new File(strDir);
//					if (!file.exists())
//					{
//						file.mkdirs();
//					}
//					String mixfileName=strDir+mFileName;
//
//
//
//
//					UploadModel uploadModel=uploadDAO.findUploadByPageAndUID(page,uuid);
//
//					UploadModel upload=new UploadModel();
//
//
//					if (uploadModel!=null){
//						Bitmap mixpic=null;
//						upload.setId(uploadModel.getId());
//						Bitmap bg= BitmapUtil.loadBitmapFromSDCard(uploadModel.getCamerapic());
//						if (bg!=null){
//							 mixpic= ImgUtil.toConformBitmap(bg,bmp);
//						}else {
//							 mixpic=bmp;
//						}
//						if (null != mixpic)
//						{
//							BitmapUtil.saveBitmapToSDCard(mixpic, mixfileName);
//						}
//
//
//
//					}
//					upload.setMixpic(mixfileName);
//
//
//					upload.setCanvaspic(filename);
//					upload.setStatus(0);
//
//					upload.setPage(page);
//					uploadDAO.update(upload);
//					List<UploadModel> list=uploadDAO.findAll();
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//			break;
//		}
//	}
	
	public void onClick(View v) {
		switch(v.getId())
		{
		case R.id.buttonpen_ID:
			OnPenClick(v);
			break;
		case  R.id.buttonnew_ID:
				OnNewClick(v);
				break;
		case R.id.buttonundo_ID:
			OnUndoClick(v);
			break;
			case R.id.buttonopen_ID:
				OnOpenClick(v);
		case R.id.buttoneraser_ID:
			OnEraserClick(v);
			break;
		case R.id.buttonredo_ID:
			onRedoClick(v);
			break;


		case R.id.buttonsave_ID:
			OnSaveClick(v);
			break;

			case R.id.upload:
				OnUploadClick(v);
				break;

			case R.id.surfaceView:
                if (!ispaizhao) {
					mCamera.takePicture(mShutter, null, mJpeg);
					ispaizhao=true;
					mWeiboDialog = WeiboDialogUtils.createLoadingDialog(SketchpadMainActivity.this, "保存中...");
				}
				break;
			default:
				break;
		}


	}




	private void OnNewClick(View v) {

		ispaizhao=false;
		SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
		String uuid = sp.getString("uid", null);
		String courseName = sp.getString("courseName", "");
		String teacherName = sp.getString("teacherName", "");
		int page=sp.getInt("page",1);
		boolean newpage = sp.getBoolean("newpage", false);
		UploadDAO uploadDAO=new UploadDAO();


		UploadModel uploadModel=uploadDAO.findLast(uuid);
		UploadModel upload=new UploadModel();
		if (uploadModel!=null){

				upload.setPage(page+1);


		}else {
			upload.setPage(1);

		}


			upload.setStatus(2);  //0已保存但未提交，1已提交 ，2未保存未提交
			upload.setCoursename(courseName);
			upload.setTeacher(teacherName);
			upload.setUid(uuid);
			uploadDAO.save(upload);


		//		UploadModel uploadBean=uploadDAO.findLast(uuid);
		final  AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
		if (newpage==false){
			//this.onCreate(null);

			builder.setMessage("还有新建课程未保存，确定要新建吗？");
			builder.setTitle("提示");
			builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					UploadDAO uploadDAO=new UploadDAO();
					SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
					String uuid = sp.getString("uid", null);

					List<UploadModel> uploadModel=uploadDAO.findByStatusAndUID(2,uuid);
					for (int i=0;i<uploadModel.size();i++){
						uploadDAO.deleteByPageAndUID(uploadModel.get(i).getPage(),uploadModel.get(i).getUid());
					}
					isone=true;

					PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
					mSurfaceView.setBackground(null);
					m_view.clearAllStrokes();
					SketchpadMainActivity.this.onCreate(null);

					dialog.dismiss();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {

					dialog.dismiss();

				}
			});
			builder.create().show();
		}else{
			isone=true;


			PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
			mSurfaceView.setBackground(null);
			m_view.clearAllStrokes();
			SketchpadMainActivity.this.onCreate(null);

		}


		SharedPreferences.Editor editor = sp.edit();
		editor.putInt("page", upload.getPage());
		editor.putBoolean("newpage", false);
		editor.commit();




	}






	private void OnSpraygunClick(View v) {
		m_view.setStrokeType(m_view.STROKE_SPRAYGUN);
	}
	private void OnSaveClick(View v) {
		mWeiboDialog= WeiboDialogUtils.createLoadingDialog(SketchpadMainActivity.this, "保存中...");

		SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();

		editor.putBoolean("newpage", true);
		editor.commit();

			try {
				String filename = null;
				File sdcarddir = android.os.Environment.getExternalStorageDirectory();
				String path=sdcarddir.getPath() + "/canvas/";
				File canvasfile = new File(path);

				filename = path+ UUID.randomUUID().toString() + ".jpg";

				if (!canvasfile.exists())
				{
					canvasfile.mkdirs();
				}


				// Bitmap bmp = m_view.getCanvasSnapshot();
				Bitmap bmp = m_view.getCanvasSnapshot();
				if (null != bmp)
				{
					BitmapUtil.saveBitmapToSDCard(bmp, filename);
				}

				String uuid = sp.getString("uid", null);
				int page=sp.getInt("page",1);


				mFileName = UUID.randomUUID().toString() + ".jpg";

				String strDir = sdcarddir.getPath() + "/mixpic/";
				File file = new File(strDir);
				if (!file.exists())
				{
					file.mkdirs();
				}
				String mixfileName=strDir+mFileName;



				UploadDAO uploadDAO=new UploadDAO();
				UploadModel uploadModel=uploadDAO.findUploadByPageAndUID(page,uuid);

				UploadModel upload=new UploadModel();


				if (uploadModel!=null){
					Bitmap mixpic=null;
					upload.setId(uploadModel.getId());
					Bitmap bg= BitmapUtil.loadBitmapFromSDCard(uploadModel.getBigpic());
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
				upload.setMixpic(mixfileName);


				upload.setCanvaspic(filename);
				upload.setStatus(0);

				upload.setPage(page);
				uploadDAO.update(upload);
				List<UploadModel> list=uploadDAO.findAll();
				isone=true;
				loadPage();
				Toast.makeText(SketchpadMainActivity.this,"保存成功", Toast.LENGTH_SHORT).show();

			} catch (Exception e) {
				Toast.makeText(SketchpadMainActivity.this,"保存失败", Toast.LENGTH_SHORT).show();

				e.printStackTrace();
			}

		mHandler.sendEmptyMessageDelayed(1, 3000);
	}





	private void OnOpenClick(View v) {
		startActivityForResult(new Intent(this, OpenGridViewActivity.class),REQUEST_TYPE_A);
		PlygonCtl.setmPoint(PlygonCtl.getStartPoint().getX(), PlygonCtl.getStartPoint().getY());
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
		Intent intent=new Intent(SketchpadMainActivity.this,GridViewColorActivity.class);
		SketchpadMainActivity.this.startActivity(intent);
	}
	
//	--------------------------ʷ����----------------------------------
	//�ж��Ƿ����˻��ƶ���ΰ�ť
	public static boolean isPlygon_Click() {
		return plygon_Click;
	}

	public static void setPlygon_Click(boolean plygon_Click) {
		SketchpadMainActivity.plygon_Click = plygon_Click;
		//���ö���α���Ϊ0  --ʷ����   2011-7-28---
		PlygonCtl.setCountLine(0);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

		// SurfaceView����ʱ������Camera��SurfaceView����ϵ
		if (mCamera != null) {
			try {
				mCamera.setPreviewDisplay(holder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


		Camera.Parameters parameters = mCamera.getParameters();// ��ȡmCamera�Ĳ�������
		Camera.Size largestSize = getBestSupportedSize(parameters
				.getSupportedPreviewSizes());
		parameters.setPreviewSize(largestSize.width, largestSize.height);// ����Ԥ��ͼƬ�ߴ�
		largestSize = getBestSupportedSize(parameters
				.getSupportedPictureSizes());// ���ò�׽ͼƬ�ߴ�
		parameters.setPictureSize(largestSize.width, largestSize.height);
		mCamera.setParameters(parameters);

		try {
			mCamera.startPreview();
		} catch (Exception e) {
			if (mCamera != null) {
				mCamera.release();
				mCamera = null;
			}
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

		if (mCamera != null) {
			mCamera.stopPreview();
		}

	}



	private final void startOrientationChangeListener() {
		mOrEventListener = new OrientationEventListener(this) {
			@Override
			public void onOrientationChanged(int rotation) {
				if (((rotation >= 0) && (rotation <= 45)) || (rotation >= 315)
						|| ((rotation >= 135) && (rotation <= 225))) {// portrait
					mCurrentOrientation = true;

				} else if (((rotation > 45) && (rotation < 135))
						|| ((rotation > 225) && (rotation < 315))) {// landscape
					mCurrentOrientation = false;

				}
			}
		};
		mOrEventListener.enable();
	}


	private Camera.Size getBestSupportedSize(List<Camera.Size> sizes) {
		// ȡ�����õ�����SIZE
		Camera.Size largestSize = sizes.get(0);
		int largestArea = sizes.get(0).height * sizes.get(0).width;
		for (Camera.Size s : sizes) {
			int area = s.width * s.height;
			if (area > largestArea) {
				largestArea = area;
				largestSize = s;
			}
		}
		return largestSize;
	}


	private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
		@Override
		public void onShutter() {
			// һ����ʾ������
		}
	};

	/* ͼ�����ݴ�����ɺ�Ļص����� */

	private Camera.PictureCallback mJpeg = new Camera.PictureCallback() {



		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// ����ͼƬ

			mFileName = UUID.randomUUID().toString() + ".jpg";

			File sdcarddir = android.os.Environment.getExternalStorageDirectory();
		String strDir = sdcarddir.getPath() + "/camerapic/";
		File file = new File(strDir);
		if (!file.exists()) {
			file.mkdirs();

		}


		String fileName=strDir+mFileName;
			if (mCurrentOrientation) {
					// ����ʱ����תͼƬ�ٱ���
					Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
							data.length);
					Matrix matrix = new Matrix();
					matrix.setRotate(90);
					Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
							oldBitmap.getWidth(), oldBitmap.getHeight(),
							matrix, true);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
				if(oldBitmap!=null){
					BitmapUtil.saveBitmapToSDCard(oldBitmap, fileName);
				}
				} else {
				Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
						data.length);
				if(oldBitmap!=null){
					BitmapUtil.saveBitmapToSDCard(oldBitmap, fileName);
				}
				}

			SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
			String uuid = sp.getString("uid", null);
			int page = sp.getInt("page", 1);

			 UploadDAO uploadDAO=new UploadDAO();
			 UploadModel uploadModel=uploadDAO.findUploadByPageAndUID(page,uuid);
             List<UploadModel> list=uploadDAO.findAll();


			UploadModel upload=new UploadModel();
			upload.setBigpic(fileName);

			if (uploadModel!=null){
				upload.setId(uploadModel.getId());

			}
			upload.setPage(page);
			uploadDAO.update(upload);

			SharedPreferences.Editor editor = sp.edit();
			editor.putInt("page", upload.getPage());
			editor.commit();

			mHandler.sendEmptyMessageDelayed(1, 3000);

//			FileOutputStream out = null;
//			try {
//				out = openFileOutput(mFileName, Context.MODE_PRIVATE);
//				byte[] newData = null;
//				if (mCurrentOrientation) {
//					// ����ʱ����תͼƬ�ٱ���
//					Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
//							data.length);
//					Matrix matrix = new Matrix();
//					matrix.setRotate(90);
//					Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
//							oldBitmap.getWidth(), oldBitmap.getHeight(),
//							matrix, true);
//					ByteArrayOutputStream baos = new ByteArrayOutputStream();
//					newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
//					newData = baos.toByteArray();
//					out.write(newData);
//				} else {
//					out.write(data);
//				}
//
//			} catch (IOException e) {
//				e.printStackTrace();
//			} finally {
//				try {
//					if (out != null)
//						out.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//			Intent i = new Intent(TestCameraActivity.this, ShowPicture.class);
//			i.putExtra(KEY_FILENAME, mFileName);
//			startActivity(i);
//			finish();
		}
	};



	@SuppressLint("NewApi")
	@Override
	public void onResume() {
		super.onResume();
		// �������
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			mCamera = Camera.open(0);
			// i=0 ��ʾ�������
		} else
			mCamera = Camera.open();
	}

	@Override
	public void onPause() {
		super.onPause();
		// �ͷ����
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}

	}
//	//�ж��Ƿ����˱��水ť
//	public static boolean isSave_Click() {
//		return save_Click;
//	}
//
//	public static void setSave_Click(boolean save_Click) {
//		SketchpadMainActivity.save_Click = save_Click;
//	}


	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode){
			case KeyEvent.KEYCODE_BACK:
				long secondTime= System.currentTimeMillis();
				if(secondTime-firstTime>2000){
					Toast.makeText(SketchpadMainActivity.this,"再按一次退出程序", Toast.LENGTH_SHORT).show();
					firstTime=secondTime;
					return true;
				}else{
					System.exit(0);
				}
				break;
		}
		return super.onKeyUp(keyCode, event);
	}






	private void OnUploadClick(View v){
		dialog(v);
		UploadDAO uploadDAO=new UploadDAO();
		SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
		String uuid = sp.getString("uid", null);
		List<UploadModel> uploadModels=uploadDAO.findByStatusAndUID(0,uuid);
		if (uploadModels!=null&&uploadModels.size()>0){
			mDialog = new CommonProgressDialog(SketchpadMainActivity.this,null);
			mDialog.setMessage("正在上传");
			mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) {

				}
			});


			uplaod(uploadModels,0);
			mDialog.show();
		}else{
			Toast.makeText(SketchpadMainActivity.this,"还没保存任何的课程", Toast.LENGTH_SHORT).show();
		}
	}



	public void uplaod(final List<UploadModel> uploadModels, int index){

        SharedPreferencesUtils sharedPreferencesUtils= SharedPreferencesUtils.createSharedPreferences("online",SketchpadMainActivity.this);

		size=uploadModels.size();
		String pic=uploadModels.get(index).getMixpic();
		String teacherName=uploadModels.get(index).getTeacher();
		String courseName=uploadModels.get(index).getCoursename();
		String uuid=uploadModels.get(index).getUid();
		String courseNum=uploadModels.get(index).getUid();

		RequestParams requestParams = new RequestParams(Constant.baseurl+"/api/letter/onlineTest.do"); // 默认编码UTF-8

		requestParams.addBodyParameter("photoUpload",pic);
		requestParams.addBodyParameter("regkey","");
		requestParams.addBodyParameter("teacherName","");
		requestParams.addBodyParameter("courseName","");
		requestParams.addBodyParameter("courseNum","");
		requestParams.addBodyParameter("uuid","");

		XHttpUtils.uploadMethod(requestParams, new Callback.CacheCallback<ResponseEntity>() {
			@Override
			public boolean onCache(ResponseEntity result) {
				return false;
			}

			@Override
			public void onSuccess(ResponseEntity result) {



				mDialog.setMax(100);
				int count=100*1024*1024/size;
				if(i==size){
					mDialog.setProgress(100);
					mDialog.dismiss();
				}else {
					mDialog.setProgress(i*count/1024/1024);

				}

				i++;
				if (i<=size){
					uplaod(uploadModels,i);

				}

			}

			@Override
			public void onError(Throwable ex, boolean isOnCallback) {
				Toast.makeText(SketchpadMainActivity.this,"上传失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancelled(CancelledException cex) {
				Toast.makeText(SketchpadMainActivity.this,"上传取消", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinished() {
				Toast.makeText(SketchpadMainActivity.this,"上传完成", Toast.LENGTH_SHORT).show();
			}
		});


	}



	private void dialog(View v) {
		final CustomDialog dialog = new CustomDialog(SketchpadMainActivity.this);
		EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
		dialog.setOnPositiveListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//dosomething youself
				dialog.dismiss();
			}
		});
		dialog.setOnNegativeListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}



	
}