package cn.yaheen.online.activity.offline;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.R;
import cn.yaheen.online.activity.online.OnlineMainActivity;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.view.MorePopWindow;
import cn.yaheen.online.view.PhotoSurfaceView;

public class TakePhotoActivity extends Activity implements SurfaceHolder.Callback {


    private SurfaceView mSurfaceView;
    private Camera mCamera;
    private ImageView moreBtn;
    private static final int PICK_PHOTO=1;
    private static final int TAKE_PHOTO=0;

    private LinearLayout photo,camera;

    private Uri photoUri;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_take_photo);

            moreBtn = (ImageView) findViewById(R.id.more);
            int sdkVersion = Build.VERSION.SDK_INT;
            if (sdkVersion > 22) {
                ActivityCompat.requestPermissions(TakePhotoActivity.this, new String[]{
                        Manifest.permission.CAMERA, Manifest.permission.LOCATION_HARDWARE}, 1);

            }
            final MorePopWindow morePopWindow = new MorePopWindow(TakePhotoActivity.this);
            moreBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    morePopWindow.showPopupWindow(moreBtn);


                }
            });

            View vw = morePopWindow.getConentView();
            photo = (LinearLayout) vw.findViewById(R.id.photo);
            camera = (LinearLayout) vw.findViewById(R.id.camera);


            photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                    startActivityForResult(intent, 1);
                }
            });


            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(TakePhotoActivity.this, "camera", Toast.LENGTH_SHORT).show();
                }
            });

            mSurfaceView = (SurfaceView) findViewById(R.id.carmer);
            SurfaceHolder holder = mSurfaceView.getHolder();
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            holder.addCallback(this); // �ص��ӿ�
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            try {
                mCamera.setPreviewDisplay(surfaceHolder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

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
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mCamera != null) {
            mCamera.stopPreview();
        }

    }




    private Camera.ShutterCallback mShutter = new Camera.ShutterCallback() {
        @Override
        public void onShutter() {
            // һ����ʾ������
        }
    };



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
	/* ͼ�����ݴ�����ɺ�Ļص����� */

    private Camera.PictureCallback mJpeg = new Camera.PictureCallback() {



        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            // ����ͼƬ

//            mFileName = UUID.randomUUID().toString() + ".jpg";
//
//            File sdcarddir = android.os.Environment.getExternalStorageDirectory();
//            String strDir = sdcarddir.getPath() + "/camerapic/";
//            File file = new File(strDir);
//            if (!file.exists()) {
//                file.mkdirs();
//
//            }
//
//
//            String fileName=strDir+mFileName;
//            if (mCurrentOrientation) {
//                // ����ʱ����תͼƬ�ٱ���
//                Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
//                        data.length);
//                Matrix matrix = new Matrix();
//                matrix.setRotate(90);
//                Bitmap newBitmap = Bitmap.createBitmap(oldBitmap, 0, 0,
//                        oldBitmap.getWidth(), oldBitmap.getHeight(),
//                        matrix, true);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                newBitmap.compress(Bitmap.CompressFormat.JPEG, 85, baos);
//                if(oldBitmap!=null){
//                    BitmapUtil.saveBitmapToSDCard(oldBitmap, fileName);
//                }
//            } else {
//                Bitmap oldBitmap = BitmapFactory.decodeByteArray(data, 0,
//                        data.length);
//                if(oldBitmap!=null){
//                    BitmapUtil.saveBitmapToSDCard(oldBitmap, fileName);
//                }
//            }
//
//            SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
//            String uuid = sp.getString("uid", null);
//            int page = sp.getInt("page", 1);
//
//            UploadDAO uploadDAO=new UploadDAO();
//            UploadModel uploadModel=uploadDAO.findUploadByPageAndUID(page,uuid);
//            List<UploadModel> list=uploadDAO.findAll();
//
//
//            UploadModel upload=new UploadModel();
//            upload.setCamerapic(fileName);
//
//            if (uploadModel!=null){
//                upload.setId(uploadModel.getId());
//
//            }
//            upload.setPage(page);
//            uploadDAO.update(upload);
//
//            SharedPreferences.Editor editor = sp.edit();
//            editor.putInt("page", upload.getPage());
//            editor.commit();


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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            switch (requestCode){
                case TAKE_PHOTO:
//                    try {
//                        Bitmap bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),photoUri);
//                        photoImageView.setImageBitmap(bitmap);
//                    }catch (IOException e){
//                        e.printStackTrace();
//                    }


                    break;
                case PICK_PHOTO:
                    //data中自带有返回的uri
                    photoUri=data.getData();
                    //获取照片路径
                    String[] filePathColumn={MediaStore.Audio.Media.DATA};
                    Cursor cursor=getContentResolver().query(photoUri,filePathColumn,null,null,null);
                    cursor.moveToFirst();
                    photoPath=cursor.getString(cursor.getColumnIndex(filePathColumn[0]));

                    cursor.close();

                    Intent intent=new Intent();
                    intent.setClass(this,OffMainActivity.class);
                    //用Bundle携带数据
                    Bundle bundle=new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("picurl",photoPath);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    Toast.makeText(TakePhotoActivity.this,photoPath,Toast.LENGTH_SHORT).show();
                    //有了照片路径，之后就是压缩图片，和之前没有什么区别

                    break;
            }
        }
    }



    /**
     * 自定义图片名，获取照片的file
     */
    private File createImgFile(){
        //创建文件
        String fileName="img_"+new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())+".jpg";//确定文件名
//        File dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File dir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File dir=Environment.getExternalStorageDirectory();
        File dir;
        if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
            dir=Environment.getExternalStorageDirectory();
        }else{
            dir=getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        }
        File tempFile=new File(dir,fileName);
        try{
            if(tempFile.exists()){
                tempFile.delete();
            }
            tempFile.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        //获取文件路径
        photoPath=tempFile.getAbsolutePath();
        return tempFile;
    }



}
