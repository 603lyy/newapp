package cn.yaheen.online.interfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

public class AndroidJSInterface {
	private Context mContext;
	public AndroidJSInterface(Context context){
		mContext = context;
	}
	
	@JavascriptInterface
    public void showResult() {
        Toast.makeText(mContext, "JS 调用 java 里的方法成功哟。", Toast.LENGTH_LONG).show();
    }
	
	@JavascriptInterface
	public String getInterface(){
		return "android_js_interface";
	}
}
