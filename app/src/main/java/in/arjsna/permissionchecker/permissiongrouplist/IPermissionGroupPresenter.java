package in.arjsna.permissionchecker.permissiongrouplist;

import in.arjsna.permissionchecker.basemvp.IMVPPresenter;
import in.arjsna.permissionchecker.models.PermissionGroupDetails;

public interface IPermissionGroupPresenter<V extends IPermissionGroupView>
    extends IMVPPresenter<V> {
  void onViewInitialised();

  PermissionGroupDetails getItemAt(int position);

  int getItemCount();
}
