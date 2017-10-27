package cn.yaheen.online.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.Contral.FileOper;
import cn.yaheen.online.R;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.UploadModel;


public class OpenGridViewActivity extends Activity {
	private GridView my_gridview ;
	private  GridImageAdapter myImageViewAdapter ;
	private FileOper fileOper = new FileOper();
	private List<UploadModel> list=null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.open_dialog);
		/* �?xml 中获�?UI 资源对象 */
		my_gridview = (GridView) findViewById(R.id.grid);
		/* 新建�?��自定义的 ImageAdapter */
		myImageViewAdapter = new GridImageAdapter(OpenGridViewActivity.this);
		/* �?GridView 对象设置�?�� ImageAdapter */
		my_gridview.setAdapter(myImageViewAdapter);

		/* �?打开对话框添加图�?Items 点击事件监听�?*/
		my_gridview.setOnItemClickListener(new OnItemClickListener() {

			
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
				UploadModel bmp = list.get(arg2);
				if (null != bmp) {
					Intent intent = new Intent();
					intent = intent.setClass(OpenGridViewActivity.this,
							SketchpadMainActivity.class);

			
					intent.putExtra("bmp", bmp.getId());
					OpenGridViewActivity.this.setResult(RESULT_OK, intent); // RESULT_OK是返回状态码
					OpenGridViewActivity.this.finish(); // 会触发onDestroy();
				}
			}
		});

	}
	

	public class GridImageAdapter extends BaseAdapter {
		/*myContext 为上下文 */
		private Context myContext ;
		/*GridView 用来加载图片�?ImageView*/
		private ImageView the_imageView ;
		// 这是图片资源 路径 的数�?



		private Bitmap[] mImageIds = fileOper.getStrokeFilePaths();
		private Bitmap[] mImageResources = null ;
		/* 构�?方法 */
		public GridImageAdapter(Context myContext) {
			this.myContext = myContext;
			UploadDAO uploadDAO=new UploadDAO();

			SharedPreferences sp = getSharedPreferences("online", Context.MODE_PRIVATE);
			String uuid = sp.getString("uid", null);
			list= uploadDAO.findByStatusAndUID(0,uuid);
		}

		
		public int getCount() {
			
//			for(int i=0; i<mImageIds.length; i++){
//				if(mImageIds[i] == null){
//					mImageResources = new Bitmap[i];
//					break;
//				}
//			}
//			for(int i=0; i<mImageResources.length; i++){
//				mImageResources[i] = mImageIds[i];
//			}
//			return mImageResources.length;


			return  list.size();
		}

		
		public Object getItem(int position) {
			return position;
		}

		
		public long getItemId(int position) {
			return position;
		}

		
		public View getView(int position, View convertView, ViewGroup parent) {
//			if (list!=null&&list.size()>0) {
//				for (int i = 0; i < list.size(); i++) {
//					if (list.get(i) == null && list.get(i).getMixpic() == null || "".equals(list.get(i).getMixpic())) {
//						mImageResources = new Bitmap[i];
//						break;
//					}
//				}
//				for (int i = 0; i < list.size(); i++) {
//					if (list.get(i).getMixpic() != null && !"".equals(list.get(i).getMixpic())) {
//						try {
//							mImageResources[i] = BitmapUtil.loadBitmapFromSDCard(list.get(i).getMixpic());
//						} catch (Exception e) {
//							mImageResources = new Bitmap[i];
//						}
//
//					} else {
//						mImageResources = new Bitmap[i];
//					}
//
//				}
//			}

            View latout= LayoutInflater.from(myContext).inflate(R.layout.coursr_item, null);
             LinearLayout linearLayout=(LinearLayout)latout.findViewById(R.id.pic);
			TextView page=(TextView)latout.findViewById(R.id.page);

//			RelativeLayout relativeLayout=(RelativeLayout)latout.findViewById(R.id.pic);
			/* 创建�?�� ImageView*/
//			the_imageView = new  ImageView( myContext );
//			/*设置图像�? */
//
			Bitmap imageResources=null;
			if (list!=null&&list.size()>0) {
				if (list.get(position).getMixpic() != null && !"".equals(list.get(position).getMixpic())) {

					try {
							imageResources= BitmapUtil.loadBitmapFromSDCard(list.get(position).getMixpic());
						} catch (Exception e) {
						}

					}
			}

			if (imageResources!=null){
				Drawable drawable=new BitmapDrawable(imageResources);
				linearLayout.setBackground(drawable);
			}else{
				linearLayout.setBackgroundResource(R.drawable.error);
			}
			page.setText("第"+list.get(position).getPage()+"页");
//
//
//			String str=String.valueOf(list.get(position).getPage());
//
//			/* �?ImageView 与边界�?�?*/
//			the_imageView .setAdjustViewBounds(true );
//			/* 设置背景图片的风�?*/
//	        the_imageView .setBackgroundResource(android.R.drawable. picture_frame );
//			/* 返回带有多个图片 ID �?ImageView*/
			return latout ;
		}

		/* 自定义获取对应位置的图片 */
		public Bitmap getcheckedImageIDPostion(int theindex) {
			return mImageResources [theindex];
		}
	}
}
