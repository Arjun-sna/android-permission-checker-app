package in.arjsna.permissionchecker;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.Callable;

/**
 * Created by arjun on 3/6/17.
 */

public class PermissionListFragment extends Fragment {
  private View mRootView;
  private PermissionGroupListAdapter permissionGroupListAdapter;
  private RecyclerView permissionsList;
  private ArrayList<PermissionGroupDetails> permissionList;
  private ProgressBar pb;

  public PermissionListFragment() {
    setHasOptionsMenu(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_permission_list, container, false);
    setUpToolBar();
    pb = (ProgressBar) mRootView.findViewById(R.id.permission_list_progress_bar);
    permissionsList = (RecyclerView) mRootView.findViewById(R.id.permission_list);
    permissionGroupListAdapter = new PermissionGroupListAdapter(getContext());
    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
    permissionsList.setLayoutManager(layoutManager);
    permissionsList.setAdapter(permissionGroupListAdapter);
    if (permissionList == null) {
      makeRx();
    } else {
      permissionGroupListAdapter.addAll(permissionList);
    }
    return mRootView;
  }

  private void makeRx() {
    Single<ArrayList<PermissionGroupDetails>> permissions =
        Single.fromCallable(() -> {
          TreeMap<String, PermissionGroupDetails> groups = fetchPermList();
          return new ArrayList<>(groups.values());
        });
    permissions.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<ArrayList<PermissionGroupDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            pb.setVisibility(View.VISIBLE);
            permissionsList.setVisibility(View.GONE);
          }

          @Override
          public void onSuccess(@NonNull ArrayList<PermissionGroupDetails> groupDetailsList) {
            Log.i("Single subscriber test ", groupDetailsList.size() + " ");
            pb.setVisibility(View.GONE);
            permissionsList.setVisibility(View.VISIBLE);
            permissionList = groupDetailsList;
            permissionGroupListAdapter.addAll(groupDetailsList);
          }

          @Override public void onError(@NonNull Throwable e) {

          }
        });
  }

  private TreeMap<String, PermissionGroupDetails> fetchPermList() {
    PackageManager packageManager = getActivity().getPackageManager();
    Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
    mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
    List<ResolveInfo> applicationInfos =
        packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
    TreeMap<String, PermissionGroupDetails> permissionGroupDetailsMap = new TreeMap<>();
    addMiscCategory(permissionGroupDetailsMap);
    addNoPermissionCategroy(permissionGroupDetailsMap);
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

  private void addNoPermissionCategroy(
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

  private void setUpToolBar() {
    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText("Permission Groups");
    toolbar.setTitle("");
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.permissionlist_menu, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.listby:
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.permission_container, new AppListFragment())
            .addToBackStack("App apps")
            .commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
}
