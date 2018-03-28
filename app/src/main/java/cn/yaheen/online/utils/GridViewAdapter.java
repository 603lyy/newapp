package cn.yaheen.online.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.yaheen.online.R;
import cn.yaheen.online.dao.EvaluateDAO;
import cn.yaheen.online.dao.UploadDAO;
import cn.yaheen.online.model.EvaluateModel;
import cn.yaheen.online.model.UploadModel;


public class GridViewAdapter extends BaseSwipeAdapter {

    private Context mContext;
    UploadDAO uploadDAO = null;
    EvaluateDAO evaluateDAO;

    public List<UploadModel> getList() {
        return list;
    }

    public void setList(List<UploadModel> list) {
        this.list = list;
    }

    private List<UploadModel> list = new ArrayList<>();

    public GridViewAdapter(Context mContext) {
        this.mContext = mContext;
        uploadDAO = new UploadDAO();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
    }

    @Override
    public void fillValues(final int position, View convertView) {
        TextView course = (TextView) convertView.findViewById(R.id.course);
        TextView teacher = (TextView) convertView.findViewById(R.id.teacher);
        ImageView trash = (ImageView) convertView.findViewById(R.id.trash);
        if (uploadDAO == null) {
            uploadDAO = new UploadDAO();

        }
        if (evaluateDAO == null) {
            evaluateDAO = new EvaluateDAO();
        }


        if (list != null && list.size() > 0) {
            final String uuid = list.get(position).getUid();
            course.setText(list.get(position).getCoursename());
            teacher.setText(list.get(position).getTeacher());

            trash.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogUtils.showNormalDialog(mContext, "确定删除本课程吗？", new DialogCallback() {
                        @Override
                        public void callback() {
                            try {
                                List<UploadModel> uploadModelList = uploadDAO.findByUID(uuid);
                                if (uploadModelList != null && !uploadModelList.isEmpty()) {
                                    for (UploadModel uploadModel : uploadModelList) {
                                        String bitmapFileName = uploadModel.getMixpic();
                                        if (bitmapFileName != null && !"".equals(bitmapFileName.trim())) {
                                            File file = new File(bitmapFileName);
                                            if (file.exists()) {
                                                file.delete();
                                            }
                                        }

                                        String pathsFileName = uploadModel.getCanvaspic();
                                        if (pathsFileName != null && !"".equals(pathsFileName.trim())) {
                                            File file = new File(pathsFileName);
                                            if (file.exists()) {
                                                file.delete();
                                            }
                                        }

                                        String bitPicFileName = uploadModel.getBigpic();
                                        if (bitPicFileName != null && !"".equals(bitPicFileName.trim())) {
                                            File file = new File(bitPicFileName);
                                            if (file.exists()) {
                                                file.delete();
                                            }
                                        }

                                    }

                                }
                                uploadDAO.deleteByUID(uuid);
                                Intent intent = new Intent();
                                intent.setAction("cn.yaheen.online");
                                intent.putExtra("msg", "ok");
                                mContext.sendBroadcast(intent); //发送广播更新界面
                            } catch (Exception e) {
                                Intent intent = new Intent();
                                intent.setAction("cn.yaheen.online");
                                intent.putExtra("msg", "error");
                                mContext.sendBroadcast(intent);
                                e.printStackTrace();
                            }

                        }
                    }, new IDialogCancelCallback() {
                        @Override
                        public void cancelCallback() {

                        }
                    }, "确定", "取消");
                }
            });
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
