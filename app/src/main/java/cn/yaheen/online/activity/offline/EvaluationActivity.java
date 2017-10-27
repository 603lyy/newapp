package cn.yaheen.online.activity.offline;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import cn.yaheen.online.R;
import cn.yaheen.online.app.OnlineApp;
import cn.yaheen.online.dao.EvaluateDAO;
import cn.yaheen.online.model.EvaluateModel;
import cn.yaheen.online.model.HttpResult;
import cn.yaheen.online.utils.Constant;
import cn.yaheen.online.utils.sharepreferences.DefaultPrefsUtil;
import cn.yaheen.online.utils.sharepreferences.SharedPreferencesUtils;
import cn.yaheen.online.utils.ToastUtils;
import cn.yaheen.online.view.CustomDialog;

public class EvaluationActivity extends Activity {

    SharedPreferencesUtils preferencesUtils=null;

    private  String uuid;
    private EditText psychosis,preparation,quality,olactivity,comment;
    private Button submit;
    private EvaluateDAO evaluateDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation);
        OnlineApp.getInstance().addActivity(this);

        preferencesUtils = SharedPreferencesUtils.createSharedPreferences("online", EvaluationActivity.this);


        Boolean screen = DefaultPrefsUtil.getIsHorizontalScreen();
        uuid=this.getIntent().getStringExtra("uuid");
        if (screen) {


            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            // setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        } else {

            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

            //
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (evaluateDAO==null){
            evaluateDAO=new EvaluateDAO();
        }
        intiview();
        setViewData();
    }


    /**
     * 加载界面组件
     */
    private void intiview(){

        psychosis=(EditText)findViewById(R.id.editPsychosis);
        preparation=(EditText)findViewById(R.id.editPreparation);
        quality=(EditText)findViewById(R.id.editQuality);
        olactivity=(EditText)findViewById(R.id.editActivity);
        comment=(EditText)findViewById(R.id.comment);
        submit=(Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final CustomDialog dialog = new CustomDialog(EvaluationActivity.this);
                final EditText editText = (EditText) dialog.getEditText();//方法在CustomDialog中实现
                dialog.setOnPositiveListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //dosomething youself
                       String courseCode=editText.getText().toString().trim();
                        giveAMark();
                        submit(courseCode);
                        dialog.dismiss();
                    }
                });
                dialog.setOnNegativeListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });
                dialog.show();

            }
        });
        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               finish();
            }
        });

    }



    void setViewData(){

        List<EvaluateModel> list=evaluateDAO.findByUID(uuid);
        if (list!=null&&list.size()>0){
            EvaluateModel evaluateModel=list.get(0);
            psychosis.setText(evaluateModel.getPsychosis());
            preparation.setText(evaluateModel.getPreparation());
            quality.setText(evaluateModel.getQuality());
            olactivity.setText(evaluateModel.getOlactivity());
            comment.setText(evaluateModel.getComment());
        }
    }

    /**
     * 保存评分
     */
    private  void giveAMark(){

        String  psychosisStr =psychosis.getText().toString().trim();
        String preparationStr=preparation.getText().toString().trim();
        String qualityStr =quality.getText().toString().trim();
        String olactivityStr=olactivity.getText().toString().trim();
        String commentStr=comment.getText().toString().trim();
        if (this.vifity()){
            if (TextUtils.isEmpty(uuid)){
                Toast.makeText(EvaluationActivity.this,"评分失败",Toast.LENGTH_SHORT).show();

            }else {
                try {
                    List<EvaluateModel> evaluateModels=evaluateDAO.findByUID(uuid);
                    if (null!=evaluateModels&&evaluateModels.size()>0){
                        EvaluateModel evaluateModel=evaluateModels.get(0);
                        if (evaluateModel!=null&&evaluateModel.getStatus()!=1){
                            EvaluateModel evaluate = new EvaluateModel();
                            evaluate.setComment(commentStr);
                            evaluate.setOlactivity(olactivityStr);
                            evaluate.setUid(uuid);
                            evaluate.setQuality(qualityStr);
                            evaluate.setPreparation(preparationStr);
                            evaluate.setPsychosis(psychosisStr);
                            evaluate.setId(evaluateModel.getId());
                            evaluateDAO.update(evaluate);

                        }else {
                            ToastUtils.showMessage(EvaluationActivity.this,"该课程已保存");

                        }
                    }else {
                        EvaluateModel evaluateModel = new EvaluateModel();
                        evaluateModel.setComment(commentStr);
                        evaluateModel.setOlactivity(olactivityStr);
                        evaluateModel.setUid(uuid);
                        evaluateModel.setQuality(qualityStr);
                        evaluateModel.setPreparation(preparationStr);
                        evaluateModel.setPsychosis(psychosisStr);
                        evaluateDAO.save(evaluateModel);
                        Toast.makeText(EvaluationActivity.this,"保存成功",Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(EvaluationActivity.this,"保存失败",Toast.LENGTH_SHORT).show();
                    e.printStackTrace();

                }
            }
        }else {
            Toast.makeText(EvaluationActivity.this,"请把表单填写完整再提交",Toast.LENGTH_SHORT).show();
        }


    }

    /**
     * 提交评分
     */
    private  void submit(String courseId){

        String psychosisStr =psychosis.getText().toString().trim();
        String preparationStr=preparation.getText().toString().trim();
        String qualityStr =quality.getText().toString().trim();
        String olactivityStr=olactivity.getText().toString().trim();
        String commentStr=comment.getText().toString().trim();
        String  teacherName= DefaultPrefsUtil.getTeacherName();
        String[] defaultFlag=new String[]{"DEF_1","DEF_2","DEF_3","DEF_4"};
        JSONArray jsonArray=new JSONArray();
        if (vifity()){

            for (int i=0;i<4;i++){
                JSONObject jsonObject=new JSONObject();
                try {
                    jsonObject.put("courseId",courseId);
                    jsonObject.put("defaultFlag",defaultFlag[i]);
                    jsonObject.put("uuid",uuid);
                    jsonObject.put("teacherName",teacherName);
                    jsonObject.put("score","10");
                    jsonObject.put("comment",commentStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonArray.put(jsonObject);
//                courseId:课程编码
//                defaultFlag:评分标准的编码(DEF_1,DEF_2…,按顺序,按照默认评分标准的)
//                uuid:传过来的标识
//                teacherName:教师名
//                score:分数
//                comment:
               // jsonResult=jsonResult+"{\"courseId\":\""+courseId+"\",\"defaultFlag\":\""+defaultFlag[i]+"\",\" uuid\":\""+uuid+"\",\"teacherName\":\""+teacherName+"\", \"score\":10 ,\"comment\":"+commentStr+"},";

            }


        }else {
            return;
        }



        Constant constant=Constant.createConstant(EvaluationActivity.this);

        String url=constant.getBaseurl()+"/eapi/score/submit.do";
        //params.addBodyParameter("courseCode",jsonResult);

        List<EvaluateModel> evaluateModels=evaluateDAO.findByUID(uuid);
        List<EvaluateModel> evaluates=evaluateDAO.findAll();
        if (null!=evaluateModels&&evaluateModels.size()>0) {
            EvaluateModel evaluateModel = evaluateModels.get(0);
            if (evaluateModel != null && evaluateModel.getStatus() != 1) {
                HttpPostData( jsonArray ,url);

            }else {
                ToastUtils.showMessage(EvaluationActivity.this,"已提交过评分");
            }
        }else {
            HttpPostData( jsonArray ,url);
        }


    }




    /**
     * 校验输入数据
     * @return
     */
    private  boolean vifity(){
        boolean result= true;
        String  psychosisStr =psychosis.getText().toString().trim();
        String preparationStr=preparation.getText().toString().trim();
        String qualityStr =quality.getText().toString().trim();
        String olactivityStr=olactivity.getText().toString().trim();
        String commentStr=comment.getText().toString().trim();

        if (TextUtils.isEmpty(psychosisStr)){
            result=false;
        }

        if (TextUtils.isEmpty(preparationStr)){
            result=false;
        }

        if (TextUtils.isEmpty(qualityStr)){
            result=false;
        }

        if (TextUtils.isEmpty(olactivityStr)){
            result=false;
        }

        if (TextUtils.isEmpty(commentStr)){
            result=false;
        }

        try{
            Integer.parseInt(psychosisStr);
            Integer.parseInt(preparationStr);
            Integer.parseInt(qualityStr);
            Integer.parseInt(olactivityStr);
        }catch (Exception e){
            result=false;
            e.printStackTrace();
        }

        return  result;
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Bundle data = msg.getData();
                String result = data.getString("value");
                Gson gson = new Gson();
                HttpResult httpResult = gson.fromJson(result, HttpResult.class);
                if (httpResult.isResult()) {
                    updateStatus(1);
                }
                ToastUtils.showMessage(EvaluationActivity.this, httpResult.getMsg());
            }catch (Exception e){
                e.printStackTrace();
            }


            // TODO
            // UI界面的更新等相关操作
        }
    };




    private  Boolean updateStatus(Integer status){
        List<EvaluateModel> evaluateModels=evaluateDAO.findByUID(uuid);
        boolean result=false;
        if (null!=evaluateModels&&evaluateModels.size()>0) {
            EvaluateModel evaluateModel = evaluateModels.get(0);
            evaluateModel.setStatus(status);
            evaluateDAO.update(evaluateModel);
            result=true;
        }

        return result;
    }

    private void HttpPostData( final JSONArray jsonArray,final  String url) {

        Runnable networkTask = new Runnable() {

            @Override
            public void run() {
                // TODO
                // 在这里进行 http request.网络请求相关操作
                Message msg = new Message();
                String result="";
                try {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(url);
                    //添加http头信息
                    httppost.addHeader("Content-Type", "application/json");
                    httppost.addHeader("User-Agent", "imgfornote");
                    //http post的json数据格式：  {"name": "your name","parentId": "id_of_parent"}
                    StringEntity sEntity = new StringEntity(jsonArray.toString(), "utf-8");

                    httppost.setEntity(sEntity);
                    HttpResponse response;
                    response = httpclient.execute(httppost);
                    //检验状态码，如果成功接收数据
                    int code = response.getStatusLine().getStatusCode();
                    if (code == 200) {
                        result =  EntityUtils.toString(response.getEntity());//返回json格式： {"id": "27JpL~j4vsL0LX00E00005","version": "abc"}
                        Log.i("dd",result);
                       //

                    }
                } catch (ClientProtocolException e) {
                    result="{\"result\":false,\"msg:\"评分失败\"}";

                } catch (IOException e) {
                    result="{\"result\":false,\"msg:\"评分失败\"}";

                } catch (Exception e) {
                    result="{\"result\":false,\"msg:\"评分失败\"}";

                }

                Bundle data = new Bundle();
                data.putString("value", result);
                msg.setData(data);
                handler.sendMessage(msg);
            }
        };
        new Thread(networkTask).start();

    }

}
