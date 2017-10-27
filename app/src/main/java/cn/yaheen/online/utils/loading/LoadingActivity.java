package cn.yaheen.online.utils.loading;



import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import cn.yaheen.online.R;
import cn.yaheen.online.receiver.Receiver;

public class LoadingActivity extends Activity implements Receiver.Message{
	Receiver myReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		setContentView(R.layout.loading);

		myReceiver = new Receiver();  //动态注册广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("cn.yaheen.load");
		registerReceiver(myReceiver, intentFilter);

		//因为这里需要注入Message，所以不能在AndroidManifest文件中静态注册广播接收器
		myReceiver.setMessage(this);

//	new Handler().postDelayed(new Runnable(){
//		public void run(){
//			//�ȴ�10000��������ٴ�ҳ�棬����ʾ��½�ɹ�
//			LoadingActivity.this.finish();
//		}
//	}, 10000);
   }

	@Override
	public void getMsg(String str) {
		LoadingActivity.this.finish();
	}
}