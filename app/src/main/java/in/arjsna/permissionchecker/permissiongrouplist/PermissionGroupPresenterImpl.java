package in.arjsna.permissionchecker.permissiongrouplist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.util.Log;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.PermissionGroupDetails;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.inject.Inject;

public class PermissionGroupPresenterImpl<V extends IPermissionGroupView> extends BasePresenter<V>
    implements IPermissionGroupPresenter<V> {

  private ArrayList<PermissionGroupDetails> permissionList;

  @Inject public PermissionGroupPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable) {
    super(context, compositeDisposable);
  }

  @Override public void onViewInitialised() {
    makeRx();
  }

  @Override public PermissionGroupDetails getItemAt(int position) {
    return permissionList != null ? permissionList.get(position) : null;
  }

  @Override public int getItemCount() {
    return permissionList != null ? permissionList.size() : 0;
  }

  private void makeRx() {
    if (permissionList != null && permissionList.size() > 0) {
      getView().notifyListAdapter();
      return;
    }
    Single.fromCallable(() -> {
      TreeMap<String, PermissionGroupDetails> groups = fetchPermList();
      return new ArrayList<>(groups.values());
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<ArrayList<PermissionGroupDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            getView().showProgressBar();
            getView().hideListView();
          }

          @Override
          public void onSuccess(@NonNull ArrayList<PermissionGroupDetails> groupDetailsList) {
            Log.i("Single subscriber test ", groupDetailsList.size() + " ");
            getView().hideProgressBar();
            getView().showListView();
            permissionList = groupDetailsList;
            getView().notifyListAdapter();
          }

          @Override public void onError(@NonNull Throwable e) {

          }
        });
  }

  private TreeMap<String, PermissionGroupDetails> fetchPermList() {
    PackageManager packageManager = getContext().getPackageManager();
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
    return permissionGroupDetailsMap;
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
