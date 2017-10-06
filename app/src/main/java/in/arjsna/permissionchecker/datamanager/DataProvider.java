package in.arjsna.permissionchecker.datamanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import in.arjsna.permissionchecker.models.PermissionGroupDetails;
import io.reactivex.Single;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class DataProvider {

  private final Context context;
  private ArrayList<PermissionGroupDetails> permissionGroupDetails;

  public DataProvider(Context context) {
    this.context = context;
  }

  public Single<ArrayList<PermissionGroupDetails>> getPermissionGroups(boolean refresh) {
    return Single.fromCallable(() -> {
      if (permissionGroupDetails == null || permissionGroupDetails.size() == 0 || refresh) {
        fetchPermList();
      }
      return permissionGroupDetails;
    });
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
              }
              continue;
            }
            if (permissionGroupDetailsMap.containsKey(permissionInfo.group)) {
              PermissionGroupDetails groupDetails =
                  permissionGroupDetailsMap.get(permissionInfo.group);
              if (groupDetails.appPackages.add(packageInfo.packageName)) {
                groupDetails.appsCount++;
              }
            } else {
              PermissionGroupDetails permissionGroupDetails = new PermissionGroupDetails();
              permissionGroupDetails.permissionGroupName = permissionInfo.group;
              permissionGroupDetails.permissionGroupDes =
                  permissionInfo.loadDescription(packageManager) == null ? "No desc"
                      : permissionInfo.loadDescription(packageManager).toString();
              if (permissionGroupDetails.appPackages.add(packageInfo.packageName)) {
                permissionGroupDetails.appsCount = 1;
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
}
