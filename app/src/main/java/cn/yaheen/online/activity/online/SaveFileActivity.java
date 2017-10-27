package cn.yaheen.online.activity.online;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.util.UUID;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.R;

public class SaveFileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_file);
        ImageView imageView=(ImageView)findViewById(R.id.imageView6);
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
            imageView.setDrawingCacheEnabled(true);
            Bitmap bmp = imageView.getDrawingCache();
            if (null != bmp)
            {
                BitmapUtil.saveBitmapToSDCard(bmp, filename);
            }


            Toast.makeText(SaveFileActivity.this,"保存成功", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(SaveFileActivity.this,"保存失败", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }
}
