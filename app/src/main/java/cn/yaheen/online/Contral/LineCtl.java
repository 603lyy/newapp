package cn.yaheen.online.Contral;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import cn.yaheen.online.interfaces.ISketchpadDraw;


public class LineCtl implements ISketchpadDraw {

	private Paint mPaint=new Paint();
    private boolean m_hasDrawn = false;  
    private float startx = 0;  
    private float starty = 0;  
    private float endx = 0;  
    private float endy = 0;  
    public LineCtl(int penSize, int penColor)
    {
    	mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(penColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(penSize);//���û��ʴ�ϸ
    }
	
	public void draw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (null != canvas)
        {
			canvas.drawLine(startx,starty,endx,endy, mPaint); 
            Log.i("sada022", "Lineʵ��");
        }
	}
	public boolean hasDraw() {
		// TODO Auto-generated method stub
		return m_hasDrawn;
		//return false;
	}
	public void cleanAll() {
		// TODO Auto-generated method stub
	}	
	public void touchDown(float x, float y) {
		// TODO Auto-generated method stub
		startx=x;
		starty=y;
		endx=x;
		endy=y;
	}
	public void touchMove(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
		m_hasDrawn=true;//��ʾ�Ѿ�������
	}
	public void touchUp(float x, float y) {
		// TODO Auto-generated method stub
		endx=x;
		endy=y;
	}

}
