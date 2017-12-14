package cn.yaheen.online.bean;

public class JBean {

    private int version;

    private String url;

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
//    RequestParams pa = new RequestParams("http://192.168.250.102:8180/aa.json");
//                x.http().get(pa, new Callback.CommonCallback<String>() {
//        @Override
//        public void onSuccess(String result) {
//            Gson g = new Gson();
//            JBean bean = g.fromJson(result, JBean.class);
//            Log.i("lin", "onSuccess: " + bean.getVersion() + "-----" + bean.getUrl());
//            Intent intent = new Intent();
//            intent.setAction(Intent.ACTION_VIEW);
//            intent.addCategory(Intent.CATEGORY_BROWSABLE);
//            intent.showVersionDialog(Uri.parse("http://192.168.250.102:8180/aa.apk"));
//            startActivity(intent);
//        }
//
//        @Override
//        public void onError(Throwable ex, boolean isOnCallback) {
//            Log.i("lin", "onError: ");
//        }
//
//        @Override
//        public void onCancelled(Callback.CancelledException cex) {
//            Log.i("lin", "onCancelled: ");
//        }
//
//        @Override
//        public void onFinished() {
//            Log.i("lin", "onFinished: ");
//        }
//    });
}
