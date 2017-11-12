package cn.edu.gdmec.android.mobileguard.m4appmanager.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.Toast;

import cn.edu.gdmec.android.mobileguard.m4appmanager.entity.AppInfo;

/**
 * Created by 周国钦 on 2017/11/12.
 */

public class EngineUtils {
    public static void shareApplication(Context context, AppInfo appInfo){
        Intent intent = new Intent("android.intent.action.SEND");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT,
                "推荐您使用一款软件，名称叫:" + appInfo.appName
                    + "下载路径:https://play.google.com/store/apps/details?id="
                    + appInfo.packageName);
        context.startActivity(intent);
    }

    public static void startApplication(Context context,AppInfo appInfo){
        PackageManager pm = context.getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(appInfo.packageName);
        if (intent != null){
            context.startActivity(intent);
        }else {
            Toast.makeText(context, "该应用没有启动界面", Toast.LENGTH_SHORT).show();
        }
    }

    public static void SettingAppDetail(Context context, AppInfo appInfo){
        Intent intent = new Intent();
        intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + appInfo.packageName));
        context.startActivity(intent);
    }

    public static void uninstallApplication(Context context, AppInfo appInfo){
        if (appInfo.isUserApp){
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_DELETE);
            intent.setData(Uri.parse("package:" + appInfo.packageName));
            context.startActivity(intent);
        }else {
            Toast.makeText(context,"系统应用无法卸载",Toast.LENGTH_LONG).show();
        }
    }

    public static void showAboutDialog(Context context, AppInfo appInfo){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("MobileGuard");	    //设置对话框标题
        builder.setMessage("Version："+"1.2"+"\nInstall time："+"2017年11月12日下午21:33:00"
                +"\nCertificate issuer："+"CN=York Cui,OU=Computer&Design College,O=Guangdong Mechanical" +
                "&Electrical Polytechnic,L=Guangzhou,ST=Guangdong,C=CN"
                +"\nPermissions："+"\nandroid.permission.INTERNET"+"\nandroid.permission.WRITE_EXTERNAL_STORAGE"
        +"\nandroid.permission.READ+PHONE_STATE"+"\nandroid.permission.RECEIVE_BOOT_COMP;ETED"
        +"\nandroid.permission.PEAD_CONTACTS");

        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序

                        break;
                }
            }
        });
        AlertDialog dialog = builder.create();	//创建对话框
        dialog.show();

    }

}
