package cn.yaheen.online.dao;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.db.table.DbModel;
import org.xutils.ex.DbException;

import java.util.List;

import cn.yaheen.online.model.UploadModel;
import cn.yaheen.online.utils.DBUtil;
import cn.yaheen.online.utils.ObjectUtils;


/**
 * Created by linjingsheng on 17/2/15.
 */

public class UploadDAO {

    private DbManager db = null;

    public UploadDAO() {
        db = DBUtil.createdatabase("dbonline");
    }

    public boolean save(UploadModel entry) {
        boolean save = false;
        try {
            db.save(entry);
            save = true;
        } catch (DbException e) {
            e.printStackTrace();

        }
        return save;
    }


    public UploadModel findById(int id) {
        UploadModel entry = null;
        try {
            entry = db.findById(UploadModel.class, id);
        } catch (DbException e) {
            e.printStackTrace();
        }
        return entry;
    }


    public List<UploadModel> findByStatus(int status) {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_status", "=", status).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<UploadModel> findByStatusAndUID(int status, String uid) {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_status", "=", status).and("fx_uid", "=", uid).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<UploadModel> findByUID(String uid) {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_uid", "=", uid).findAll();

        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<UploadModel> findAll() {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<UploadModel> findByUIDNoCommit(String uid) {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_uid", "=", uid).and("fx_status", "=", 0).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public List<UploadModel> findByUIDCommit(String uid) {
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_uid", "=", uid).and("fx_status", "=", 1).findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


    public void updateByStatus(int status, int id) {


        try {
            UploadModel uploadModel = db.findById(UploadModel.class, id);
            uploadModel.setStatus(1);
            db.update(uploadModel, "fx_status");

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    /**
     * Create by xszyou on 20170616:更新所有的状态
     */
    public Boolean updateAllStatus(int status, String uuid) {
        try {
            int a = db.update(UploadModel.class, WhereBuilder.b("fx_uid", "=", uuid), new KeyValue("fx_status", status));
            return true;
        } catch (DbException e) {
            return false;
        }
    }


    public UploadModel findUploadByPageAndUID(int page, String uid) {

        UploadModel uploadModel = null;
        List<UploadModel> list = null;
        try {
            uploadModel = db.selector(UploadModel.class).where("fx_uid", "=", uid).and("fx_page", "=", page).findFirst();
        } catch (DbException e) {
            e.printStackTrace();
        }

        return uploadModel;
    }


    public boolean update(UploadModel entry) {

        boolean update = false;
        try {

            UploadModel uploadModel = db.findById(UploadModel.class, entry.getId());
            if (uploadModel != null) {
                ObjectUtils.mergeObject(entry, uploadModel);
                db.update(uploadModel);
                update = true;
            }


        } catch (DbException e) {
            e.printStackTrace();
        }

        return update;
    }


    public void deleteByPageAndUID(int page, String uid) {


        UploadModel uploadModel = null;

        try {
            uploadModel = db.selector(UploadModel.class).where("fx_page", "=", page).and("fx_uid", "=", uid).findFirst();
            db.delete(uploadModel);

            //Edit by xszyou on 20170625:更新page
            List<UploadModel> models = db.selector(UploadModel.class).where("fx_uid", "=", uid)
                    .and("fx_page", ">", uploadModel.getPage()).findAll();
            if (models != null && !models.isEmpty()) {
                for (UploadModel um : models) {
                    um.setPage(um.getPage() - 1);
                    db.update(um);
                }
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }


    public void deleteByUID(String uid) {


        List<UploadModel> list = null;

        try {
            list = db.selector(UploadModel.class).where("fx_uid", "=", uid).findAll();
            db.delete(list);

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    public void deleteAll() {


        List<UploadModel> list = null;

        try {
            list = db.selector(UploadModel.class).findAll();
            db.delete(list);

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    public UploadModel findLast(String uid) {

        UploadModel uploadModel = null;
        List<UploadModel> list = null;
        try {
            list = db.selector(UploadModel.class).where("fx_uid", "=", uid).orderBy("fx_page").findAll();
            if (list != null && list.size() > 0) {
                uploadModel = list.get(list.size() - 1);
            }
        } catch (DbException e) {
            e.printStackTrace();
        }

        return uploadModel;
    }


    public List<DbModel> findAllByGruopUUID() {
        List<DbModel> list = null;
        try {
            list = db.selector(UploadModel.class).groupBy("fx_uid").findAll();
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<DbModel> findAllByPage(int pageSize, int pageNum) {
        List<DbModel> list = null;
        try {
            list = db.selector(UploadModel.class).groupBy("fx_uid").limit(pageSize).offset(pageNum - 1).findAll();
            ;
        } catch (DbException e) {
            e.printStackTrace();
        }
        return list;
    }


}
