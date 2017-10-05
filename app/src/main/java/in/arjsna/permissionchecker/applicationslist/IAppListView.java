package in.arjsna.permissionchecker.applicationslist;

import in.arjsna.permissionchecker.basemvp.IMVPView;

public interface IAppListView extends IMVPView {
  void setTitle(String title);

  void showProgressBar();

  void hideListView();

  void hideProgressBar();

  void showListView();

  void notifyListAdapter();

}
