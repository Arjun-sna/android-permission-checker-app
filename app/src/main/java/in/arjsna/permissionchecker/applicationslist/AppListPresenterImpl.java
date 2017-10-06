package in.arjsna.permissionchecker.applicationslist;

import android.content.Context;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.AppDetails;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import javax.inject.Inject;

public class AppListPresenterImpl<V extends IAppListView> extends BasePresenter<V>
    implements IAppListPresenter<V> {

  private ArrayList<AppDetails> appDetailList;
  private ArrayList<String> packages;

  @Inject public AppListPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable, DataProvider dataProvider) {
    super(context, compositeDisposable, dataProvider);
  }

  private void getAppDetails() {
    if (appDetailList != null) {
      return;
    }
    getView().showProgressBar();
    getView().hideListView();

    getCompositeDisposable().add(getDataProvider().getAppDetailsList(packages, false)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<ArrayList<AppDetails>>() {
          @Override public void onSuccess(ArrayList<AppDetails> appDetails) {
            getView().hideProgressBar();
            getView().showListView();
            if (appDetailList != appDetails) {
              appDetailList = appDetails;
              getView().notifyListAdapter();
            }
          }

          @Override public void onError(Throwable e) {

          }
        }));
  }

  @Override public void onIntentDataAvailable(ArrayList<String> packages) {
    this.packages = packages;
  }

  @Override public void onViewInitialised() {
    getView().setTitle(packages == null ? "All Installed Apps" : "App List");
    getAppDetails();
  }

  @Override public AppDetails getItemAt(int position) {
    return appDetailList == null ? null : appDetailList.get(position);
  }

  @Override public int getItemCount() {
    return appDetailList == null ? 0 : appDetailList.size();
  }

  @Override public void onListItemClicked(int adapterPosition) {
    getView().showFullDetails(appDetailList.get(adapterPosition), adapterPosition);
  }

  @Override public void onItemRemoved(int positionOfAppInList) {
    appDetailList.remove(positionOfAppInList);
    getView().notifyItemRemoved(positionOfAppInList);
  }
}
