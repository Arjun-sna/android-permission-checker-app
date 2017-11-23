package in.arjsna.permissionchecker.appdetails;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.AppDetails;
import in.arjsna.permissionchecker.models.PermissionDetail;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.io.File;
import java.util.ArrayList;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;

public class AppDetailsPresenterImpl<V extends IAppDetailsView> extends BasePresenter<V>
    implements IAppDetailsPresenter<V> {

  private ArrayList<PermissionDetail> permissionDetails;
  private AppDetails appDetails;
  private boolean canReplaceExistingFile = false;

  @Inject public AppDetailsPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable, DataProvider dataProvider) {
    super(context, compositeDisposable, dataProvider);
  }

  @Override public void onViewInitialised() {

  }

  @Override public void onIntentDataAvailable(String mPackageName) {
    fetchDetails(mPackageName, false);
  }

  @Override public PermissionDetail getItemAt(int position) {
    return permissionDetails == null ? null : permissionDetails.get(position);
  }

  @Override public int getItemCount() {
    return permissionDetails == null ? 0 : permissionDetails.size();
  }

  private void fetchDetails(final String packageName, boolean refresh) {
    getCompositeDisposable().add(getDataProvider().getAppDetailsFor(packageName, refresh)
        .doOnSuccess(this::setAppDetails)
        .map(appDetails -> {
          ArrayList<PermissionDetail> permissionDetails = new ArrayList<>();
          fillData(permissionDetails, appDetails);
          return permissionDetails;
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<ArrayList<PermissionDetail>>() {
          @Override public void onSuccess(ArrayList<PermissionDetail> permissionDetails) {
            getView().setAppIcon(appDetails.icon);
            getView().setAppName(appDetails.name);
            getView().setPackageName(appDetails.packageName);
            if (permissionDetails.size() == 0) {
              getView().setLabelText("No permissions required");
              return;
            }
            getView().setPermissionCount(
                appDetails.grantedPermissionList.size() + appDetails.deniedPermissionList.size());
            setPermissionDetails(permissionDetails);
            getView().notifyAdapter();
          }

          @Override public void onError(Throwable e) {

          }
        }));
  }

  private void fillData(ArrayList<PermissionDetail> permissionDetails, AppDetails appDetails) {
    if (appDetails.grantedPermissionList.size() == 0
        && appDetails.deniedPermissionList.size() == 0) {
      return;
    }
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
  }

  private void setAppDetails(AppDetails appDetails) {
    this.appDetails = appDetails;
  }

  private void setPermissionDetails(ArrayList<PermissionDetail> permissionDetails) {
    this.permissionDetails = permissionDetails;
  }

  @Override public void onDataChanged() {
    getDataProvider().refreshData();
  }

  @Override public void onSettingsChanged(String packageName) {
    fetchDetails(packageName, true);
  }

  @Override public void extractAndSaveApk() {
    getView().requestForStoragePermission();
  }

  @Override public void onPermissionDenied() {
    getView().showError("APK extraction failed");
  }

  @Override public void onPermissionGranted() {
    extractAPK();
  }

  @Override public void extractByReplacing() {
    canReplaceExistingFile = true;
    extractAPK();
  }

  private void extractAPK() {
    getCompositeDisposable().add(createDirectoryAndExtractApk().subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<String>() {
          @Override public void onSuccess(String filePath) {
            getView().onExtractionComplete(filePath);
          }

          @Override public void onError(Throwable e) {
            if (e instanceof IllegalStateException) {
              getView().showFileExitsAlert();
              return;
            }
            getView().showError("APK extraction failed");
          }
        }));
  }

  private Single<String> createDirectoryAndExtractApk() {
    return Single.create(e -> {
      String appDirPath = createDirectory(e);
      File apkFile = new File(appDetails.publicSrcDir);
      File destFile = new File(appDirPath + "/" + appDetails.name + ".apk");
      if (!destFile.exists() || canReplaceExistingFile) {
        FileUtils.copyFile(apkFile, destFile);
        e.onSuccess(destFile.getPath());
      } else {
        e.onError(new IllegalStateException("File already exits"));
      }
    });
  }



  @NonNull private String createDirectory(SingleEmitter<String> e) {
    File appDir =
        new File(Environment.getExternalStorageDirectory() + "/AppPermissionsExtractedApk");
    if (!appDir.exists()) {
      if (!appDir.mkdir()) {
        e.onError(new Exception("Error creating directory"));
      }
    }
    return appDir.getPath();
  }
}
