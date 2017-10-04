package in.arjsna.permissionchecker;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by arjun on 7/6/17.
 */

public class AppDetailsFragment extends Fragment {
  private static final int UNINSTALL_APP_REQUEST = 500;
  private View mRootView;
  private ImageView appIcon;
  private TextView packageNameTv;
  private TextView appName;
  private RecyclerView permissionsList;
  private PermissionListAdapter permissionListAdapter;
  private TextView noPermissionLabel;
  private TextView openAppBtn;
  private TextView appDetails;
  private String mPackageName;
  private TextView uninstall;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_details, container, false);
    setUpToolBar();
    mPackageName = getArguments().getString("package_name");
    initialiseViews();
    bindEvents();
    fetchDetails(mPackageName);
    return mRootView;
  }

  private void bindEvents() {
    openAppBtn.setOnClickListener(v -> {
      Intent openAppIntent =
          getActivity().getPackageManager().getLaunchIntentForPackage(mPackageName);
      startActivity(openAppIntent);
    });
    appDetails.setOnClickListener(v -> {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      Uri uri = Uri.fromParts("package", mPackageName, null);
      intent.setData(uri);
      startActivity(intent);
    });
    RxView.clicks(uninstall).subscribe(o -> {
      Intent intent =
          new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", mPackageName, null));
      intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
      startActivityForResult(intent, UNINSTALL_APP_REQUEST);
    });
  }

  private void initialiseViews() {
    openAppBtn = mRootView.findViewById(R.id.open_app);
    appDetails = mRootView.findViewById(R.id.app_details);
    uninstall = mRootView.findViewById(R.id.app_uninstall);
    appIcon = mRootView.findViewById(R.id.app_picture);
    appName = mRootView.findViewById(R.id.app_name);
    noPermissionLabel = mRootView.findViewById(R.id.detail_label);
    packageNameTv = mRootView.findViewById(R.id.package_string);
    permissionsList = mRootView.findViewById(R.id.permission_list);
    permissionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    permissionListAdapter = new PermissionListAdapter(getActivity());
    permissionsList.setAdapter(permissionListAdapter);
  }

  private void fetchDetails(final String packageName) {
    Single<AppDetails> appDetailsSingle = Single.fromCallable(() -> {
      AppDetails appDetails = new AppDetails();
      PackageManager packageManager = getActivity().getPackageManager();
      PackageInfo packageInfo = packageManager.getPackageInfo(packageName,
          PackageManager.GET_META_DATA | PackageManager.GET_PERMISSIONS);
      appDetails.name = packageInfo.applicationInfo.loadLabel(packageManager).toString();
      appDetails.icon = packageInfo.applicationInfo.loadIcon(packageManager);
      appDetails.packageName = packageName;
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
      return appDetails;
    });
    appDetailsSingle.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<AppDetails>() {
          @Override public void onSubscribe(@NonNull Disposable d) {

          }

          @Override public void onSuccess(@NonNull AppDetails appDetails) {
            appIcon.setImageDrawable(appDetails.icon);
            appName.setText(appDetails.name);
            packageNameTv.setText(appDetails.packageName);
            if (appDetails.grantedPermissionList.size() == 0
                && appDetails.deniedPermissionList.size() == 0) {
              noPermissionLabel.setText("No permissions required");
            } else {
              noPermissionLabel.setText(
                  getString(R.string.permission_count, appDetails.grantedPermissionList.size()));
              ArrayList<PermissionDetail> permissionDetails = new ArrayList<>();
              PermissionDetail section1 = new PermissionDetail();
              section1.viewType = PermissionDetail.VIEW_TYPE_SECTION;
              section1.sectionName = "Granted Permissions";
              permissionDetails.add(section1);
              for (String perm : appDetails.grantedPermissionList) {
                PermissionDetail permissionDetail = new PermissionDetail();
                permissionDetail.permissionName = perm;
                permissionDetail.isGranted = true;
                permissionDetail.viewType = PermissionDetail.VIEW_TYPE_ITEM;
                permissionDetails.add(permissionDetail);
              }
              PermissionDetail section2 = new PermissionDetail();
              section2.viewType = PermissionDetail.VIEW_TYPE_SECTION;
              section2.sectionName = "Denied Permissions";
              permissionDetails.add(section2);
              for (String perm : appDetails.deniedPermissionList) {
                PermissionDetail permissionDetail = new PermissionDetail();
                permissionDetail.permissionName = perm;
                permissionDetail.isGranted = false;
                permissionDetail.viewType = PermissionDetail.VIEW_TYPE_ITEM;
                permissionDetails.add(permissionDetail);
              }

              permissionListAdapter.addAllAndNotify(permissionDetails);
            }
          }

          @Override public void onError(@NonNull Throwable e) {
            e.printStackTrace();
          }
        });
  }

  private void setUpToolBar() {
    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
    toolbar.setTitle("");
    TextView titleTextView = toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText("App Details");
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    supportActionBar.setDisplayHomeAsUpEnabled(true);
    supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == UNINSTALL_APP_REQUEST) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          getActivity().onBackPressed();
          break;
        case Activity.RESULT_CANCELED:
          Toast.makeText(getContext(), "Cancelled.", Toast.LENGTH_LONG).show();
          break;
        default:
          Toast.makeText(getContext(), "Failed to uninstall the app.", Toast.LENGTH_LONG).show();
      }
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }
}
