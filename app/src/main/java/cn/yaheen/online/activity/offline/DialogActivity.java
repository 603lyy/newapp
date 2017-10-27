package cn.yaheen.online.activity.offline;


import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import cn.yaheen.online.R;
import cn.yaheen.online.app.OnlineApp;

/**
 * @author yangyu
 *	��������������Activity����
 */
public class DialogActivity extends Activity implements OnClickListener{
	private LinearLayout layout01,layout02,layout03,layout04;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dialog);
		OnlineApp.getInstance().addActivity(this);
		initView();
	}

	/**
	 * ��ʼ�����
	 */
	private void initView(){
		//�õ���������������ü����¼�
		layout01 = (LinearLayout)findViewById(R.id.llayout01);
		layout02 = (LinearLayout)findViewById(R.id.llayout02);
		layout03 = (LinearLayout)findViewById(R.id.llayout03);
		layout04 = (LinearLayout)findViewById(R.id.llayout04);

		layout01.setOnClickListener(this);
		layout02.setOnClickListener(this);
		layout03.setOnClickListener(this);
		layout04.setOnClickListener(this);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		finish();
		return true;
	}
	
	@Override
	public void onClick(View v) {
		
	}
}
