package in.arjsna.permissionchecker.applicationslist;

import in.arjsna.permissionchecker.basemvp.IMVPView;
import in.arjsna.permissionchecker.models.AppDetails;

public interface IAppListView extends IMVPView {
  void setTitle(String title);

  void showProgressBar();

  void hideListView();

  void hideProgressBar();

  void showListView();

  void notifyListAdapter();

  void showFullDetails(AppDetails appDetails, int position);
}
