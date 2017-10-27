package cn.yaheen.online.activity.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.soundcloud.android.crop.Crop;
import java.io.File;
import cn.yaheen.online.R;
import cn.yaheen.online.utils.UriPath;
import me.panavtec.drawableview.DrawableView;
import me.panavtec.drawableview.DrawableViewConfig;

public class TestActivity extends Activity {

    private DrawableView drawableView;
    private DrawableViewConfig config = new DrawableViewConfig();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);

        drawableView = (DrawableView) findViewById(R.id.paintView);
        config.setStrokeColor(getResources().getColor(android.R.color.black));
        config.setShowCanvasBounds(true);
        config.setStrokeWidth(20.0f);
        config.setMinZoom(1.0f);
        config.setMaxZoom(3.0f);
        config.setCanvasHeight(1000);
        config.setCanvasWidth(1920);
        drawableView.setConfig(config);



    }




}
