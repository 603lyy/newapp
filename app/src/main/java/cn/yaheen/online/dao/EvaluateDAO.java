package cn.yaheen.online.dao;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.List;

import cn.yaheen.online.model.EvaluateModel;
import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.DBUtil;
import cn.yaheen.online.utils.ObjectUtils;

/**
 * Created by linjingsheng on 17/4/27.
 */

public class EvaluateDAO {

    private DbManager db=null;

    public EvaluateDAO() {
        db = DBUtil.createdatabase("dbonline");
    }



    public  boolean save(EvaluateModel entry){
        boolean save=false;
        try {
            db.save(entry);
            save=true;
        } catch (DbException e) {
            e.printStackTrace();

        }
        return  save;
    }


    public  EvaluateModel findById(int id){
        EvaluateModel entry=null;
        try {
            entry=   db.findById(EvaluateModel.class,id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return  entry;
    }



    public  boolean update(EvaluateModel entry){

        boolean update=false;
        try {

            EvaluateModel evaluateModel = db.findById(EvaluateModel.class, entry.getId());
            if (evaluateModel!=null){
                ObjectUtils.mergeObject(entry,evaluateModel);
                db.update(evaluateModel);
                update=true;
            }


        } catch (DbException e) {
            e.printStackTrace();
        }

        return  update;
    }


    public  void deleteByUID(int id){

        EvaluateModel evaluateModel=null;

        try {
            evaluateModel=   db.selector(EvaluateModel.class).where("fx_id","=",id).findFirst();
            db.delete(evaluateModel);
        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    public List<EvaluateModel> findByUID(String uid){
        List<EvaluateModel> list=null;
        try {
            list=   db.selector(EvaluateModel.class).where("fx_uid","=",uid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return  list;
    }

    public List<EvaluateModel> findAll(){
        List<EvaluateModel> list=null;
        try {
            list=   db.selector(EvaluateModel.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return  list;
    }
}
