package cn.yaheen.online.utils;

import android.graphics.Bitmap;
import android.os.Environment;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import cn.yaheen.online.Contral.BitmapUtil;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.image.ImgUtil;

/**
 * Created by linjingsheng on 17/3/20.
 */

public class MixPic extends  Thread {

    private List<UploadModel> uploadModels=null; //定义需要传值进来的参数

    public Bitmap getResult() {
        return result;
    }

    private   Bitmap result=null;
    public MixPic(List<UploadModel> uploadModels){
        this.uploadModels = uploadModels;
    }


    private MixPic(){

    }


    @Override
    public void run() {


        if (uploadModels!=null&&uploadModels.size()>0){
            Bitmap[]  bitmaps=new Bitmap[uploadModels.size()];
            Comparator<UploadModel> comparator = new Comparator<UploadModel>() {
                public int compare(UploadModel s1, UploadModel s2) {


                    return  s1.getPage()-s2.getPage();
                }
            };
            Collections.sort(uploadModels,comparator);

            if (uploadModels!=null&&uploadModels.size()>0){

                for (int i=0;i<uploadModels.size();i++){

                    if (i==0){
                        if (uploadModels.get(i).getBigpic()!=null&&!"".equals(uploadModels.get(i).getBigpic())){
                            Bitmap first= BitmapUtil.loadBitmapFromSDCard(uploadModels.get(i).getBigpic());
                            Bitmap second=BitmapUtil.loadBitmapFromSDCard(uploadModels.get(i).getCanvaspic());
                            bitmaps[i]= ImgUtil.mixPictrue(second,first);
                        }else {
                            bitmaps[i]=BitmapUtil.loadBitmapFromSDCard(uploadModels.get(i).getCanvaspic());
                        }

                    }else {

                        Bitmap second=BitmapUtil.loadBitmapFromSDCard(uploadModels.get(i).getCanvaspic());
                        bitmaps[i]=ImgUtil.mixPictrue(second,bitmaps[i-1]);
                        String filename = Environment.getExternalStorageDirectory() + "/canvas/"  + "test123.jpg";

                        if (null != bitmaps[i]) {
                            BitmapUtil.saveBitmapToSDCard(bitmaps[i], filename);
                        }
                    }
                }


            }

            result=  bitmaps[uploadModels.size()-1];
        }








    }
}
