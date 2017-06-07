package in.arjsna.permissionchecker;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by arjun on 3/6/17.
 */

public class PermissionListFragment extends Fragment {
  private View mRootView;
  private PermissionListAdapter permissionListAdapter;
  private RecyclerView permissionsList;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_permission_list, container, false);
    permissionsList = (RecyclerView) mRootView.findViewById(R.id.permission_list);
    permissionListAdapter = new PermissionListAdapter(getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    permissionsList.setLayoutManager(layoutManager);
    permissionsList.setAdapter(permissionListAdapter);
    makeRx();
    //logAllPer();
    //fetchPermInfo();
    return mRootView;
  }

  private void makeRx() {
    Single<List<PermissionGroupDetails>> permissions =
        Single.fromCallable(new Callable<List<PermissionGroupDetails>>() {
          @Override public List<PermissionGroupDetails> call() throws Exception {
            Map<String, PermissionGroupDetails> groups = fetchPermList();
            return new ArrayList<>(groups.values());
          }
        });
    permissions.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<PermissionGroupDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {

          }

          @Override public void onSuccess(@NonNull List<PermissionGroupDetails> strings) {
            Log.i("Single subscriber test ", strings.size() + " ");
            permissionListAdapter.addAll(strings);
          }

          @Override public void onError(@NonNull Throwable e) {

          }
        });
  }

  private Map<String, ArrayList<String>> fetchPermInfo() {
    PackageManager packageManager = getActivity().getPackageManager();
    List<ApplicationInfo> applicationInfos =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    Map<String, ArrayList<String>> permAppMap = new HashMap<>();
    for (ApplicationInfo applicationInfo : applicationInfos) {
      Log.d("test", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);

      try {
        PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName,
            PackageManager.GET_PERMISSIONS);

        //Get Permissions
        PermissionInfo[] requestedPermissions = packageInfo.permissions;
        if (requestedPermissions != null) {
          for (PermissionInfo permissionInfo : requestedPermissions) {
            Log.d("*************", "**************************************************");
            Log.d("test************ ", permissionInfo.group + " ");
            Log.d("test************ ", permissionInfo.name + " ");
            //Log.d("test************ ", permissionInfo.loadDescription(packageManager).toString() + " ");
            Log.d("*************", "**************************************************");
            if (permissionInfo.group != null) {
              ArrayList<String> appList = permAppMap.get(permissionInfo.group);
              if (appList != null) {
                appList.add(applicationInfo.packageName);
              } else {
                appList = new ArrayList<>();
                appList.add(applicationInfo.packageName);
                permAppMap.put(permissionInfo.group, appList);
              }
            } else {
              ArrayList<String> appList = permAppMap.get("misc");
              if (appList != null) {
                appList.add(applicationInfo.packageName);
              } else {
                appList = new ArrayList<>();
                appList.add(applicationInfo.packageName);
                permAppMap.put("misc", appList);
              }
            }
          }
        }
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }
    return permAppMap;
  }

  private Map<String, PermissionGroupDetails> fetchPermList() {
    PackageManager packageManager = getActivity().getPackageManager();
    List<ApplicationInfo> applicationInfos =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    Map<String, PermissionGroupDetails> permissionGroupDetailsMap = new HashMap<>();
    PermissionGroupDetails miscPermissionGroup = new PermissionGroupDetails();
    miscPermissionGroup.permissionGroupName = "MISC";
    miscPermissionGroup.permissionGroupName = "Custom/Miscellaneous permissions";
    miscPermissionGroup.appsCount = 0;
    permissionGroupDetailsMap.put("MISC", miscPermissionGroup);
    for (ApplicationInfo applicationInfo : applicationInfos) {
      try {
        PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName,
            PackageManager.GET_PERMISSIONS);

        PermissionInfo[] requestedPermissions = packageInfo.permissions;
        if (requestedPermissions != null) {
          for (PermissionInfo permissionInfo : requestedPermissions) {
            if (permissionInfo.group == null) {
              PermissionGroupDetails groupDetails = permissionGroupDetailsMap.get("MISC");
              groupDetails.appsCount++;
              groupDetails.appPackages.add(applicationInfo.packageName);
              continue;
            }
            if (permissionGroupDetailsMap.containsKey(permissionInfo.group)) {
              PermissionGroupDetails groupDetails =
                  permissionGroupDetailsMap.get(permissionInfo.group);
              groupDetails.appsCount++;
              groupDetails.appPackages.add(applicationInfo.packageName);
            } else {
              PermissionGroupDetails permissionGroupDetails = new PermissionGroupDetails();
              permissionGroupDetails.permissionGroupName = permissionInfo.group;
              permissionGroupDetails.permissionGroupDes =
                  permissionInfo.loadDescription(packageManager) == null ? "No desc"
                      : permissionInfo.loadDescription(packageManager).toString();
              permissionGroupDetails.appsCount = 1;
              permissionGroupDetails.appPackages.add(applicationInfo.packageName);
              permissionGroupDetailsMap.put(permissionInfo.group, permissionGroupDetails);
            }
          }
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
    Log.i("Map size ", permissionGroupDetailsMap.size() + " ");
    return permissionGroupDetailsMap;
  }
}
