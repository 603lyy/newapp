package cn.yaheen.online.model;

import android.support.annotation.NonNull;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Comparator;

/**
 * Created by linjingsheng on 17/2/15.
 */
@Table(name = "tb_upload",onCreated = "")
public class UploadModel   {

    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private  int id;
    @Column(name = "fx_teacher")
    private String teacher;

    @Column(name = "fx_coursename")
    private String coursename;

    @Column(name = "fx_page")
    private  int page;

    @Column(name = "fx_camerapic")
    private String bigpic;

    @Column(name = "fx_canvaspic")
    private String canvaspic;

    @Column(name = "fx_mixpic")
    private String mixpic;

    @Column(name = "fx_status")
    private  int status;//0：未保存本地、1：已保存、2：已上传
    public final static int STATUS_NOT_SAVE = 0;
    public final static int STATUS_SAVED = 1;
    public final static int STATUS_UPLOADED = 2;


    @Column(name = "fx_uid")
    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getId() {
        return id;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getCoursename() {
        return coursename;
    }

    public int getPage() {
        return page;
    }


    public String getCanvaspic() {
        return canvaspic;
    }

    public String getMixpic() {
        return mixpic;
    }

    public int getStatus() {
        return status;
    }

    public String getBigpic() {
        return bigpic;
    }

    public void setBigpic(String bigpic) {
        this.bigpic = bigpic;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public void setCoursename(String coursename) {
        this.coursename = coursename;
    }

    public void setPage(int page) {
        this.page = page;
    }


    public void setCanvaspic(String canvaspic) {
        this.canvaspic = canvaspic;
    }

    public void setMixpic(String mixpic) {
        this.mixpic = mixpic;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "UploadModel{" +
                "id=" + id +
                ", teacher='" + teacher + '\'' +
                ", coursename='" + coursename + '\'' +
                ", page=" + page +
                ", bigpic='" + bigpic + '\'' +
                ", canvaspic='" + canvaspic + '\'' +
                ", mixpic='" + mixpic + '\'' +
                ", status=" + status +
                '}';
    }


}
