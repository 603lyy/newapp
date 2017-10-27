package cn.yaheen.online.utils.loading;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import cn.yaheen.online.R;

public class MainActivity extends Activity implements OnClickListener{

	private Button main_login_btn;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        main_login_btn = (Button) this.findViewById(R.id.main_login_btn);
        main_login_btn.setOnClickListener(this);
    }

	public void onClick(View v) {
		Intent intent = new Intent();
        intent.setClass(MainActivity.this,LoadingActivity.class);//��ת�����ؽ���
        startActivity(intent);		
	}
    
}
