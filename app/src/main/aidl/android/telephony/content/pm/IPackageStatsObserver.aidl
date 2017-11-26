// IPackageStatsObserver.aidl
package android.telephony.content.pm;

// Declare any non-default types here with import statements
import android.telephony.content.pm.IPackageDataObserver;
oneway interface IPackageStatsObserver{
    void onGetStatsCompleted(in PackageStats pStats, boolean succeeded);
}