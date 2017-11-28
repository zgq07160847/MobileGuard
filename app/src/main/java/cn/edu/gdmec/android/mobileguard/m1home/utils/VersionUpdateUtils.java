package cn.edu.gdmec.android.mobileguard.m1home.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.edu.gdmec.android.mobileguard.R;
import cn.edu.gdmec.android.mobileguard.m1home.HomeActivity;
import cn.edu.gdmec.android.mobileguard.m1home.entity.VersionEntity;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by 周国钦 on 2017/10/16.
 */

public class VersionUpdateUtils {
    private String mVersion;
    private Activity context;
    private VersionEntity versionEntity;
    
    private static final int MESSAGE_IO_ERROR = 102;
    private static final int MESSAGE_JSON_ERROR = 103;
    private static final int MESSAGE_SHOW_DIALOG = 104;
    private static final int MESSAGE_ENTERHOME = 105;

    //下一个activtiy的class
    private Class<?> nextActivty;
    //下载完成后的回调
    private DownloadCallback downloadCallback;
    //下载任务的id
    private long downloadId;
    //下载完毕的广播接收者
    private BroadcastReceiver broadcastReceiver;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            switch (msg.what){
                case MESSAGE_IO_ERROR:
                    Toast.makeText(context,"IO错误",Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_JSON_ERROR:
                    Toast.makeText(context,"JSON解析错误",Toast.LENGTH_LONG).show();
                    break;
                case MESSAGE_SHOW_DIALOG:
                    showUpdateDialog(versionEntity);
                    break;
                case MESSAGE_ENTERHOME:
                    if(nextActivty!=null) {
                        Intent intent = new Intent(context, nextActivty);
                        context.startActivity(intent);
                        context.finish();
                    }
                    break;
            }
        }
    };

    public VersionUpdateUtils(String mVersion,Activity context,DownloadCallback downloadCallback,Class<?> nextActivty){
        this.mVersion = mVersion;
        this.context = context;
        this.downloadCallback = downloadCallback;
        this.nextActivty = nextActivty;
    }

    public void getCloudVersion(String url){
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpConnectionParams.setConnectionTimeout(httpclient.getParams(),5000);
            HttpConnectionParams.setSoTimeout(httpclient.getParams(),5000);
            HttpGet httpGet = new HttpGet(url);
            HttpResponse execute = httpclient.execute(httpGet);
            if (execute.getStatusLine().getStatusCode()==200){
                HttpEntity httpEntity = execute.getEntity();
                String result = EntityUtils.toString(httpEntity,"utf-8");
                System.out.println(result);
                JSONObject jsonObject = new JSONObject(result);
                versionEntity = new VersionEntity();
                String code = jsonObject.getString("code");
                versionEntity.versionCode = code;
                String des = jsonObject.getString("des");
                versionEntity.description = des;
                String apkurl = jsonObject.getString("apkurl");
                versionEntity.apkurl = apkurl;
                if (!mVersion.equals(versionEntity.versionCode)){
                    handler.sendEmptyMessage(MESSAGE_SHOW_DIALOG);
                }
            }
        } catch (IOException e) {
            handler.sendEmptyMessage(MESSAGE_IO_ERROR);
            e.printStackTrace();
        } catch (JSONException e) {
            handler.sendEmptyMessage(MESSAGE_JSON_ERROR);
            e.printStackTrace();
        }
    }
    private void showUpdateDialog(final VersionEntity versionEntity){
        AlertDialog.Builder builer = new AlertDialog.Builder(context);
        builer.setTitle("检查到有新版本:"+versionEntity.versionCode);
        builer.setMessage(versionEntity.description);
        builer.setCancelable(false);
        builer.setIcon(R.mipmap.ic_launcher_round);
        builer.setPositiveButton("立即升级", new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface,int i){
                downloadNewApk(versionEntity.apkurl);
            }
        });
        builer.setNegativeButton("暂不升级",new DialogInterface.OnClickListener(){
            public void onClick(DialogInterface dialogInterface,int i){
                dialogInterface.dismiss();
                enterHome();
            }
        });
        builer.show();
    }
    private void enterHome(){
        handler.sendEmptyMessageDelayed(MESSAGE_ENTERHOME,2000);
    }
    private void downloadNewApk(String apkurl){
        DownloadUtils downLoadUtils = new DownloadUtils();
        String filename = "downloadfile";
        String suffixes="avi|mpeg|3gp|mp3|mp4|wav|jpeg|gif|jpg|png|apk|exe|pdf|rar|zip|docx|doc|apk|db";
        Pattern pat=Pattern.compile("[\\w]+[\\.]("+suffixes+")");//正则判断
        Matcher mc=pat.matcher(apkurl);//条件匹配
        while(mc.find()){
            filename = mc.group();//截取文件名后缀名
        }
        downapk(apkurl, filename, context);
    }
    public void downapk(String url,String targetFile,Context context){

        //创建下载任务
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setAllowedOverRoaming(false);//漫游网络是否可以下载

        //设置文件类型，可以在下载结束后自动打开该文件
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        String mimeString = mimeTypeMap.getMimeTypeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
        request.setMimeType(mimeString);

        //在通知栏中显示，默认就是显示的
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        request.setVisibleInDownloadsUi(true);

        //sdcard的目录下的download文件夹，必须设置
        request.setDestinationInExternalPublicDir("/download/", targetFile);
        //request.setDestinationInExternalFilesDir(),也可以自己制定下载路径

        //将下载请求加入下载队列
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        //加入下载队列后会给该任务返回一个long型的id，
        //通过该id可以取消任务，重启任务等等，看上面源码中框起来的方法
        downloadId = downloadManager.enqueue(request);
        listener(downloadId,targetFile);
    }

    private void listener(final long Id,final String filename) {

        // 注册广播监听系统的下载完成事件。
        IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (ID == Id) {
                    Toast.makeText(context.getApplicationContext(), "下载编号:" + Id +"的"+filename+" 下载完成!", Toast.LENGTH_LONG).show();
                }
                context.unregisterReceiver(broadcastReceiver);
                downloadCallback.afterDownload(filename);
            }
        };

        context.registerReceiver(broadcastReceiver, intentFilter);
    }
    public interface DownloadCallback{
        void afterDownload(String filename);
    }
}
