package in.arjsna.permissionchecker.permissiongrouplist;

import in.arjsna.permissionchecker.basemvp.IMVPView;

public interface IPermissionGroupView extends IMVPView {
  void showProgressBar();

  void hideListView();

  void hideProgressBar();

  void showListView();

  void notifyListAdapter();
}
