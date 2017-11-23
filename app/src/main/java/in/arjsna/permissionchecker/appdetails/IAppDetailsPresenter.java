package in.arjsna.permissionchecker.appdetails;

import in.arjsna.permissionchecker.basemvp.IMVPPresenter;
import in.arjsna.permissionchecker.models.PermissionDetail;

public interface IAppDetailsPresenter<V extends IAppDetailsView> extends IMVPPresenter<V> {
  void onViewInitialised();

  void onIntentDataAvailable(String mPackageName);

  PermissionDetail getItemAt(int position);

  int getItemCount();

  void onDataChanged();

  void onSettingsChanged(String mPackageName);

  void extractAndSaveApk();

  void onPermissionDenied();

  void onPermissionGranted();

  void extractByReplacing();
}
