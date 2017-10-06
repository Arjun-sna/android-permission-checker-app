package in.arjsna.permissionchecker.appdetails;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.AppDetails;
import in.arjsna.permissionchecker.models.PermissionDetail;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;

public class AppDetailsPresenterImpl<V extends IAppDetailsView> extends BasePresenter<V>
    implements IAppDetailsPresenter<V> {

  private ArrayList<PermissionDetail> permissionDetails;

  @Inject public AppDetailsPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable, DataProvider dataProvider) {
    super(context, compositeDisposable, dataProvider);
  }

  @Override public void onViewInitialised() {

  }

  @Override public void onIntentDataAvailable(String mPackageName) {
    fetchDetails(mPackageName);
  }

  @Override public PermissionDetail getItemAt(int position) {
    return permissionDetails == null ? null : permissionDetails.get(position);
  }

  @Override public int getItemCount() {
    return permissionDetails == null ? 0 : permissionDetails.size();
  }

  private void fetchDetails(final String packageName) {
    Single<AppDetails> appDetailsSingle = Single.fromCallable(() -> {
      AppDetails appDetails = new AppDetails();
      PackageManager packageManager = getContext().getPackageManager();
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
            getView().setAppIcon(appDetails.icon);
            getView().setAppName(appDetails.name);
            getView().setPackageName(appDetails.packageName);
            if (appDetails.grantedPermissionList.size() == 0
                && appDetails.deniedPermissionList.size() == 0) {
              getView().setLabelText("No permissions required");
            } else {
              getView().setPermissionCount(
                  appDetails.grantedPermissionList.size() + appDetails.deniedPermissionList.size());
              permissionDetails = new ArrayList<>();
              if (appDetails.grantedPermissionList.size() > 0) {
                PermissionDetail section1 = new PermissionDetail();
                section1.viewType = PermissionDetail.VIEW_TYPE_SECTION;
                section1.sectionName = "Granted Permissions";
                permissionDetails.add(section1);
                for (String perm : appDetails.grantedPermissionList) {
                  PermissionDetail permissionDetail = new PermissionDetail();
                  permissionDetail.permissionName = perm.replace("android.permission.", "");
                  permissionDetail.isGranted = true;
                  permissionDetail.viewType = PermissionDetail.VIEW_TYPE_ITEM;
                  permissionDetails.add(permissionDetail);
                }
              }
              if (appDetails.deniedPermissionList.size() > 0) {
                PermissionDetail section2 = new PermissionDetail();
                section2.viewType = PermissionDetail.VIEW_TYPE_SECTION;
                section2.sectionName = "Denied Permissions";
                permissionDetails.add(section2);
                for (String perm : appDetails.deniedPermissionList) {
                  PermissionDetail permissionDetail = new PermissionDetail();
                  permissionDetail.permissionName = perm.replace("android.permission.", "");
                  permissionDetail.isGranted = false;
                  permissionDetail.viewType = PermissionDetail.VIEW_TYPE_ITEM;
                  permissionDetails.add(permissionDetail);
                }
              }
              getView().notifyAdapter();
            }
          }

          @Override public void onError(@NonNull Throwable e) {
            e.printStackTrace();
          }
        });
  }
}
