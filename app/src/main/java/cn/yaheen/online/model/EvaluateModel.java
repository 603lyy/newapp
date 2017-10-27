package cn.yaheen.online.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by linjingsheng on 17/4/27.
 */

@Table(name = "tb_evaluate",onCreated = "")
public class EvaluateModel {


    @Column(name = "id",isId = true,autoGen = true,property = "NOT NULL")
    private  int id;
    @Column(name = "fx_psychosis")
    private  String psychosis;

    @Column(name = "fx_preparation")
    private  String preparation;

    @Column(name = "fx_quality")
    private  String quality;

    @Column(name = "fx_olactivity")
    private  String olactivity;

    @Column(name = "fx_comment")
    private  String comment;

    @Column(name = "fx_uid")
    private String uid;

    @Column(name = "fx_status")
    private  int status;

    public String getUid() {
        return uid;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
    public String getPsychosis() {
        return psychosis;
    }

    public String getPreparation() {
        return preparation;
    }

    public String getQuality() {
        return quality;
    }

    public String getOlactivity() {
        return olactivity;
    }

    public String getComment() {
        return comment;
    }

    public void setPsychosis(String psychosis) {
        this.psychosis = psychosis;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setOlactivity(String olactivity) {
        this.olactivity = olactivity;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }


}
