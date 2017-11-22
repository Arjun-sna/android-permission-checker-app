package in.arjsna.permissionchecker.appdetails;

import android.graphics.drawable.Drawable;
import in.arjsna.permissionchecker.basemvp.IMVPView;

public interface IAppDetailsView extends IMVPView {
  void setAppIcon(Drawable icon);

  void setAppName(String name);

  void setPackageName(String packageName);

  void setLabelText(String s);

  void setPermissionCount(int count);

  void notifyAdapter();

  void requestForStoragePermission();

  void onExtractionComplete(String s);

  void showError(String s);
}
