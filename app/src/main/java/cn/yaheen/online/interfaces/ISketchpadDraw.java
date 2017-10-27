package cn.yaheen.online.interfaces;

import android.graphics.Canvas;

//ʵ�ֻ�ͼ�ӿ�
public interface ISketchpadDraw {

	public void draw(Canvas canvas);
	public boolean hasDraw();
	public void cleanAll();
	public void touchDown(float x, float y);
	public void touchMove(float x, float y);
	public void touchUp(float x, float y);
}
