package in.arjsna.permissionchecker.permissiongrouplist;

import android.content.Context;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.PermissionGroupDetails;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import javax.inject.Inject;

public class PermissionGroupPresenterImpl<V extends IPermissionGroupView> extends BasePresenter<V>
    implements IPermissionGroupPresenter<V> {

  private ArrayList<PermissionGroupDetails> permissionList;

  @Inject public PermissionGroupPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable, DataProvider dataProvider) {
    super(context, compositeDisposable, dataProvider);
    System.out.println(dataProvider.toString());
  }

  @Override public void onViewInitialised() {
    fetchData();
  }

  @Override public PermissionGroupDetails getItemAt(int position) {
    return permissionList != null ? permissionList.get(position) : null;
  }

  @Override public int getItemCount() {
    return permissionList != null ? permissionList.size() : 0;
  }

  private void fetchData() {
    getView().showProgressBar();
    getView().hideListView();
    getCompositeDisposable().add(getDataProvider().getPermissionGroups(false)
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribeWith(new DisposableSingleObserver<ArrayList<PermissionGroupDetails>>() {
          @Override
          public void onSuccess(ArrayList<PermissionGroupDetails> permissionGroupDetails) {
            getView().hideProgressBar();
            getView().showListView();
            permissionList = permissionGroupDetails;
            getView().notifyListAdapter();
          }

          @Override public void onError(Throwable e) {

          }
        }));
  }
}
