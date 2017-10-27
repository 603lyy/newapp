package cn.yaheen.online.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.media.Image;
import android.media.ImageReader;
import android.os.Environment;
import android.util.Base64;
import android.view.View;
import android.webkit.WebView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.nio.ByteBuffer;

import cn.yaheen.online.R;

/**
 * Created by linjingsheng on 17/2/16.
 */

public class ImgUtil {

    public static Bitmap toConformBitmap(Bitmap background, Bitmap foreground) {
        if (background == null) {
            return null;
        }

        int bgWidth = background.getWidth();
        int bgHeight = background.getHeight();
        int fgWidth = foreground.getWidth();
        int fgHeight = foreground.getHeight();
        //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图
        Bitmap newbmp = Bitmap.createBitmap(bgWidth + fgWidth, bgHeight, Bitmap.Config.ARGB_8888);
        Canvas cv = new Canvas(newbmp);
        //draw bg into
        cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg
        //draw fg into
        cv.drawBitmap(foreground, bgWidth, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
        //save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);//保存
        //store
        cv.restore();//存储
        return newbmp;
    }


    /**
     * @param bitmap1 //下面
     * @param bitmap2 //上面的图片
     * @return
     */
    public static Bitmap mixPictrue(Bitmap bitmap1, Bitmap bitmap2) {
// 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap newBitmap = null;


        // newBitmap = Bitmap.createBitmap(bitmap1);
        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();
        int w_2 = bitmap2.getWidth();
        int h_2 = bitmap2.getHeight();
        int realbmh = (w / w_2) * h_2;
        newBitmap = Bitmap.createBitmap(w, h + realbmh, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();

        paint.setColor(Color.GRAY);
        paint.setAlpha(125);
        Bitmap realBitmap2 = blow(bitmap2, w / w_2);
        canvas.drawBitmap(realBitmap2, 0, 0, paint);
        paint = new Paint();
        canvas.drawBitmap(bitmap1, 0, realbmh, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
// 存储新合成的图片
        canvas.restore();

        return newBitmap;
    }


    /**
     * 根绝宽度大小等比放大高度
     *
     * @return
     */
    public static Bitmap showBigByWidth(int width, Bitmap bitmap) {
        float height = bitmap.getHeight() * (width * 1f / bitmap.getWidth());
        Bitmap thum = ImgUtil.big(bitmap, width, height);
        return thum;
    }



    /*
    等比放大
     */

    public static Bitmap blow(Bitmap src, int multiple) {


        Matrix matrix = new Matrix();
        matrix.postScale(multiple, multiple); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), matrix, true);

        return resizeBmp;
    }


    /**
     * 根据长宽放大
     *
     * @param b
     * @param x
     * @param y
     * @return
     */
    public static Bitmap big(Bitmap b, float x, float y) {
        int w = b.getWidth();
        int h = b.getHeight();
        float sx = (float) x / w;//要强制转换，不转换我的在这总是死掉。
        float sy = (float) y / h;
        Matrix matrix = new Matrix();
        matrix.postScale(sx, sy); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(b, 0, 0, w,
                h, matrix, true);
        return resizeBmp;
    }


    /**
     * @param bitmap1 //下面
     * @param bitmap2 //上面的图片
     * @return
     */
    public static Bitmap mixPictrueOnSelfSize(Bitmap bitmap1, Bitmap bitmap2) {
// 防止出现Immutable bitmap passed to Canvas constructor错误
        Bitmap newBitmap = null;


        // newBitmap = Bitmap.createBitmap(bitmap1);
        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();
        int w_2 = bitmap2.getWidth();
        int h_2 = bitmap2.getHeight();
        newBitmap = Bitmap.createBitmap(w, h + h_2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();

        paint.setColor(Color.GRAY);
        paint.setAlpha(125);
        canvas.drawBitmap(bitmap2, 0, 0, paint);
        paint = new Paint();
        canvas.drawBitmap(bitmap1, 0, h_2, paint);
        canvas.save(Canvas.ALL_SAVE_FLAG);
// 存储新合成的图片
        canvas.restore();

        return newBitmap;
    }

    /**
     * 将图片转为二进制
     *
     * @param bitmap
     * @return
     */
    public static byte[] getBitmapByte(Bitmap bitmap) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        try {
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    /**
     * 对webview进行截屏
     *
     * @param webView
     * @return
     */
    public static Bitmap captureWebView(WebView webView) {
//        获取webview缩放率
        float scale = webView.getScale();
//        得到缩放后webview内容的高度
        int webViewHeight = (int) (webView.getContentHeight() * scale);
        Bitmap bitmap = Bitmap.createBitmap(webView.getWidth(), webViewHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
//绘制
        webView.draw(canvas);
        return bitmap;
    }

    /**
     * base64转为bitmap
     *
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        byte[] bytes = Base64.decode(base64Data, Base64.DEFAULT);
        BitmapFactory.Options options = new BitmapFactory.Options();
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    /**
     * 对屏幕进行截图，只支持5.0版本以上
     *
     * @param mImageReader 屏幕图片读取器
     * @param bitmapX      截图截取屏幕的X起始点
     * @param bitmapY      截图截取屏幕的Y起始点
     * @param bitmapWidth  截图的宽度
     * @param bitmapHeight 截图的高度
     * @param path         图片存储的路径
     * @return 成功返回bitmap，失败返回空
     */
    public static Bitmap startCapture(ImageReader mImageReader, String path, int bitmapX, int bitmapY, int bitmapWidth, int bitmapHeight) {

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            Image image = mImageReader.acquireLatestImage();
            if (image == null) {
                return null;
            }

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;

            Bitmap mBitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            mBitmap.copyPixelsFromBuffer(buffer);
            mBitmap = Bitmap.createBitmap(mBitmap, bitmapX, bitmapY, bitmapWidth, bitmapHeight);
            image.close();

            if (mBitmap != null) {
                try {
                    File file = new File(path);
                    FileOutputStream os = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                    return mBitmap;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
