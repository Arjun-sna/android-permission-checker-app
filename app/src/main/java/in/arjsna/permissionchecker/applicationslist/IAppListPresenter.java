package in.arjsna.permissionchecker.applicationslist;

import in.arjsna.permissionchecker.models.AppDetails;
import in.arjsna.permissionchecker.basemvp.IMVPPresenter;
import java.util.ArrayList;

public interface IAppListPresenter<V extends IAppListView> extends IMVPPresenter<V> {
  void onIntentDataAvailable(ArrayList<String> packages);

  void onViewInitialised();

  AppDetails getItemAt(int position);

  int getItemCount();

  void onListItemClicked(int adapterPosition);
}
