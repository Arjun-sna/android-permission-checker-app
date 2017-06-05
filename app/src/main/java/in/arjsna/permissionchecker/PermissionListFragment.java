package in.arjsna.permissionchecker;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    Single<List<PermissionDetails>> permissions = Single.fromCallable(new Callable<List<PermissionDetails>>() {
      @Override public List<PermissionDetails> call() throws Exception {
        Set<String> groups = fetchPermList();
        ArrayList<PermissionDetails> list = new ArrayList<>();
        for (String s : groups) {
          Log.i("//////////////", "s");
          PermissionDetails permissionDetails = new PermissionDetails();
          permissionDetails.permissionName = s;
          list.add(permissionDetails);
        }
        return list;
      }
    });
    permissions.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<PermissionDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {

          }

          @Override public void onSuccess(@NonNull List<PermissionDetails> strings) {
            Log.i("Single subscriber test " , strings.size() + " ");
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


  private Set<String> fetchPermList() {
    PackageManager packageManager = getActivity().getPackageManager();
    List<ApplicationInfo> applicationInfos =
        packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    Set<String> permissions = new HashSet<>();
    for (ApplicationInfo applicationInfo : applicationInfos) {
      //Log.d("test", "App: " + applicationInfo.name + " Package: " + applicationInfo.packageName);

      try {
        PackageInfo packageInfo = packageManager.getPackageInfo(applicationInfo.packageName,
            PackageManager.GET_PERMISSIONS);

        //Get Permissions
        PermissionInfo[] requestedPermissions = packageInfo.permissions;
        if (requestedPermissions != null) {
          for (PermissionInfo permissionInfo : requestedPermissions) {
            permissions.add(permissionInfo.group);
          }
        }
      } catch (PackageManager.NameNotFoundException e) {
        e.printStackTrace();
      }
    }
    return permissions;
  }
}
