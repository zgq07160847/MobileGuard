package cn.edu.gdmec.android.mobileguard.m4appmanager.entity;

import android.graphics.drawable.Drawable;

/**
 * Created by 周国钦 on 2017/11/12.
 */

public class AppInfo {
    public String packageName;
    public Drawable icon;
    public String appName;
    public String apkPath;
    public long appSize;
    public boolean isInRoom;
    public boolean isUserApp;
    public boolean isSelected = false;
    public String certificateIssuer;
    public String appVersion;
    public String appPermissions;
    public String installTime;
    public String version;
    public String InstallTime;
    public String signature;
    public String permissions;
    public String activityName;
    public  boolean isLock;

    public String getAppLocation(boolean isInRoom){
        if (isInRoom){
            return "手机内存";
        }else {
            return "外部存储";
        }
    }
}
