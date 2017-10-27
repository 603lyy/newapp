package cn.yaheen.online.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by linjingsheng on 17/4/27.
 */

public class EvaluateParm {



    public String getCourseId() {
        return courseId;
    }

    public String getDefaultFlag() {
        return defaultFlag;
    }

    public String getUuid() {
        return uuid;
    }

    public String getTeacherName() {
        return teacherName;
    }

    public String getScore() {
        return score;
    }

    public String getComment() {
        return comment;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public void setDefaultFlag(String defaultFlag) {
        this.defaultFlag = defaultFlag;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTeacherName(String teacherName) {
        this.teacherName = teacherName;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    private  String courseId;
    private  String defaultFlag;
    private  String uuid;
    private  String teacherName;
    private  String score;
    private  String comment;






}
