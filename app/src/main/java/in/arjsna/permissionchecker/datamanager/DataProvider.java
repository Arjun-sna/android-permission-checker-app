package in.arjsna.permissionchecker.datamanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.util.Log;
import in.arjsna.permissionchecker.models.AppDetails;
import in.arjsna.permissionchecker.models.PermissionGroupDetails;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Callable;

public class DataProvider {

  private final Context context;
  private ArrayList<PermissionGroupDetails> permissionGroupDetails;
  private ArrayList<AppDetails> allAppDetailList;
  private Map<String, AppDetails> packageDetailsMap;

  public DataProvider(Context context) {
    this.context = context;
  }

  public Single<AppDetails> getAppDetailsFor(String packageName, boolean refresh) {
    return Single.fromCallable(() -> {
      if (!refresh) {
        return packageDetailsMap.get(packageName);
      } else {
        AppDetails appDetails = fetchDetail(packageName);
        packageDetailsMap.put(packageName, appDetails);
        return appDetails;
      }
    });
  }

  public Single<ArrayList<PermissionGroupDetails>> getPermissionGroups(boolean refresh) {
    return Single.fromCallable(() -> {
      if (permissionGroupDetails == null || permissionGroupDetails.size() == 0 || refresh) {
        fetchPermList();
      }
      return permissionGroupDetails;
    });
  }

  public Single<ArrayList<AppDetails>> getAppDetailsList(boolean refresh) {
    return getAppDetailsList(null, refresh);
  }

  public Single<ArrayList<AppDetails>> getAppDetailsList(ArrayList<String> packages,
      boolean refresh) {
    return Single.fromCallable(() -> {
      if (packageDetailsMap == null || packageDetailsMap.size() == 0 || refresh) {
        fetchAllAppDetails();
      }
      return getDetailsFor(packages);
    });
  }

  private ArrayList<AppDetails> getDetailsFor(ArrayList<String> packages) {
    if (packages == null) {
      return allAppDetailList;
    } else {
      ArrayList<AppDetails> appDetails = new ArrayList<>();
      for (String packageName : packages) {
        if (packageDetailsMap.containsKey(packageName)) {
          appDetails.add(packageDetailsMap.get(packageName));
        }
      }
      return appDetails;
    }
  }

  private void fetchAllAppDetails() {
    allAppDetailList = new ArrayList<>();
    packageDetailsMap = new TreeMap<>();
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    PackageManager packageManager = context.getPackageManager();
    List<ResolveInfo> applicationInfos =
        packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
    for (ResolveInfo resolveInfo : applicationInfos) {
      AppDetails appDetails = fetchDetail(resolveInfo.activityInfo.packageName);
      allAppDetailList.add(appDetails);
      packageDetailsMap.put(resolveInfo.activityInfo.packageName, appDetails);
    }
  }

  private AppDetails fetchDetail(String packageName) {
    PackageManager packageManager = context.getPackageManager();
    AppDetails appDetails = new AppDetails();
    try {
      PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
          PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
      appDetails.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
      appDetails.icon = packageInfo.applicationInfo.loadIcon(packageManager);
      appDetails.packageName = packageName;
      appDetails.publicSrcDir = packageInfo.applicationInfo.publicSourceDir;
      if (packageInfo.requestedPermissions != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          for (int index = 0; index < packageInfo.requestedPermissions.length; index++) {
            if ((packageInfo.requestedPermissionsFlags[index]
                & PackageInfo.REQUESTED_PERMISSION_GRANTED) != 0) {
              appDetails.grantedPermissionList.add(packageInfo.requestedPermissions[index]);
            } else {
              appDetails.deniedPermissionList.add(packageInfo.requestedPermissions[index]);
            }
          }
        } else {
          appDetails.grantedPermissionList =
              new ArrayList<>(Arrays.asList(packageInfo.requestedPermissions));
        }
      }
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return appDetails;
  }

  private void fetchPermList() {
    PackageManager packageManager = context.getPackageManager();
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> applicationInfos =
        packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
    TreeMap<String, PermissionGroupDetails> permissionGroupDetailsMap = new TreeMap<>();
    addMiscCategory(permissionGroupDetailsMap);
    addNoPermissionCategory(permissionGroupDetailsMap);
    int totalAppsCount = applicationInfos.size();
    for (ResolveInfo applicationInfo : applicationInfos) {
      try {
        PackageInfo packageInfo =
            packageManager.getPackageInfo(applicationInfo.activityInfo.packageName,
                PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
        String[] requestedPermissions = packageInfo.requestedPermissions;
        if (requestedPermissions != null) {
          for (String permission : requestedPermissions) {
            PermissionInfo permissionInfo =
                packageManager.getPermissionInfo(permission, PackageManager.GET_META_DATA);
            if (permissionInfo.group == null) {
              PermissionGroupDetails groupDetails = permissionGroupDetailsMap.get("z_MISC");
              if (groupDetails.appPackages.add(packageInfo.packageName)) {
                groupDetails.appsCount++;
                groupDetails.percentage = (groupDetails.appsCount / (float) totalAppsCount) * 100;
              }
              continue;
            }
            if (permissionGroupDetailsMap.containsKey(permissionInfo.group)) {
              PermissionGroupDetails groupDetails =
                  permissionGroupDetailsMap.get(permissionInfo.group);
              if (groupDetails.appPackages.add(packageInfo.packageName)) {
                groupDetails.appsCount++;
                groupDetails.percentage = (groupDetails.appsCount / (float) totalAppsCount) * 100;
              }
            } else {
              PermissionGroupDetails permissionGroupDetails = new PermissionGroupDetails();
              permissionGroupDetails.permissionGroupName = permissionInfo.group;
              permissionGroupDetails.permissionGroupDes =
                  permissionInfo.loadDescription(packageManager) == null ? "No desc"
                      : permissionInfo.loadDescription(packageManager).toString();
              if (permissionGroupDetails.appPackages.add(packageInfo.packageName)) {
                permissionGroupDetails.appsCount = 1;
                permissionGroupDetails.percentage = (permissionGroupDetails.appsCount / (float)totalAppsCount) * 100;
              }
              permissionGroupDetailsMap.put(permissionInfo.group, permissionGroupDetails);
            }
          }
        } else {
          PermissionGroupDetails groupDetails = permissionGroupDetailsMap.get("z_NO_PERMISSION");
          if (groupDetails.appPackages.add(packageInfo.packageName)) {
            groupDetails.appsCount++;
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Log.i("Map size ", permissionGroupDetailsMap.size() + " ");
    permissionGroupDetails = new ArrayList<>(permissionGroupDetailsMap.values());
  }

  private void addNoPermissionCategory(
      TreeMap<String, PermissionGroupDetails> permissionGroupDetailsMap) {
    PermissionGroupDetails miscPermissionGroup = new PermissionGroupDetails();
    miscPermissionGroup.permissionGroupDes = "App don't need any permission";
    miscPermissionGroup.permissionGroupName = "NO PERMISSIONS REQUIRED";
    miscPermissionGroup.appsCount = 0;
    permissionGroupDetailsMap.put("z_NO_PERMISSION", miscPermissionGroup);
  }

  private void addMiscCategory(TreeMap<String, PermissionGroupDetails> permissionGroupDetailsMap) {
    PermissionGroupDetails miscPermissionGroup = new PermissionGroupDetails();
    miscPermissionGroup.permissionGroupDes = "Custom/Miscellaneous permissions";
    miscPermissionGroup.permissionGroupName = "MISC PERMISSION";
    miscPermissionGroup.appsCount = 0;
    permissionGroupDetailsMap.put("z_MISC", miscPermissionGroup);
  }

  public void refreshData() {
    fetchPermList();
    fetchAllAppDetails();
  }
}
