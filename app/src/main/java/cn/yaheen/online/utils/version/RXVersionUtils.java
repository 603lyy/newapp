//package cn.yaheen.online.utils.version;
//
//import android.content.Intent;
//import android.os.Build;
//import android.support.annotation.NonNull;
//
//import org.reactivestreams.Subscriber;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.concurrent.TimeUnit;
//
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//import okhttp3.OkHttpClient;
//import okhttp3.ResponseBody;
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
//
///**
// * Created by Administrator on 2017/12/13.
// */
//
//public class RXVersionUtils {
//
//    private static final String TAG = "DownloadUtils";
//
//    private static final int DEFAULT_TIMEOUT = 15;
//
//    private Retrofit retrofit;
//
//    private DownloadListener listener;
//
//    private String filePath;
//
//    private String baseUrl;
//
//    public RXVersionUtils(String baseUrl, DownloadListener listener) {
//
//        this.baseUrl = baseUrl;
//        this.listener = listener;
//
//        //自己定义的拦截器
//        DownloadInterceptor mInterceptor = new DownloadInterceptor(listener);
//
//        OkHttpClient httpClient = new OkHttpClient.Builder()
//                .addInterceptor(mInterceptor)
//                .retryOnConnectionFailure(true)
//                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
//                .build();
//
//        retrofit = new Retrofit.Builder()
//                .baseUrl(baseUrl)
//                .client(httpClient)
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .build();
//    }
//
//    /**
//     * 开始下载
//     *
//     * @param url
//     * @param filePath
//     */
//    public void download(@NonNull String url, final String filePath) {
//
//        listener.onStartDownload();
//        this.filePath = filePath;
//
//        // subscribeOn()改变调用它之前代码的线程
//        // observeOn()改变调用它之后代码的线程
//        retrofit.create(DownloadService.class)
//                .download(url)
//                .subscribeOn(Schedulers.io())
//                .unsubscribeOn(Schedulers.io())
//                .map(new Func1<ResponseBody, InputStream>() {
//
//                    @Override
//                    public InputStream call(ResponseBody responseBody) {
//                        return responseBody.byteStream();
//                    }
//                })
//                .observeOn(Schedulers.computation()) // 用于计算任务
//                .doOnNext(new Action1<InputStream>() {
//                    @Override
//                    public void call(InputStream inputStream) {
//                        writeFile(inputStream, filePath);
//                    }
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(subscriber);
//    }
//
//    private Subscriber subscriber = new Subscriber() {
//        @Override
//        public void onCompleted() {
//            listener.onFinishDownload(filePath);
//        }
//
//        @Override
//        public void onError(Throwable e) {
//
//        }
//
//        @Override
//        public void onNext(Object o) {
//
//        }
//    };
//
//    /**
//     * 将输入流写入文件
//     *
//     * @param inputString
//     * @param filePath
//     */
//    private void writeFile(InputStream inputString, String filePath) {
//
//        File file = new File(filePath);
//        if (file.exists()) {
//            file.delete();
//        }
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//
//            byte[] b = new byte[1024];
//
//            int len;
//            while ((len = inputString.read(b)) != -1) {
//                fos.write(b, 0, len);
//            }
//            inputString.close();
//            fos.close();
//
//        } catch (FileNotFoundException e) {
//            listener.onFail("FileNotFoundException");
//        } catch (IOException e) {
//            listener.onFail("IOException");
//        }
//
//    }
//
//    /**
//     * @param file
//     * @return
//     * @Description 安装apk
//     */
//    protected void installApk(File file) {
//        Intent intent = new Intent(Intent.ACTION_VIEW);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { // 7.0+以上版本
////            Uri apkUri = FileProvider.getUriForFile(context, "com.shawpoo.app.fileprovider", file);  //包名.fileprovider
////            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
////            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
////        } else {
////            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
////        }
////        context.startActivity(intent);
//        }
//    }
//}
