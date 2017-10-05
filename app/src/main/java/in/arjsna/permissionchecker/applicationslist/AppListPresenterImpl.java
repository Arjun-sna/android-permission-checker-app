package in.arjsna.permissionchecker.applicationslist;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import in.arjsna.permissionchecker.basemvp.BasePresenter;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.models.AppDetails;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

public class AppListPresenterImpl<V extends IAppListView> extends BasePresenter<V>
    implements IAppListPresenter<V> {

  private ArrayList<AppDetails> appDetailList;
  private ArrayList<String> packages;

  @Inject public AppListPresenterImpl(@ActivityContext Context context,
      CompositeDisposable compositeDisposable) {
    super(context, compositeDisposable);
  }

  private void getAppDetails() {
    if (appDetailList != null && appDetailList.size() > 0) {
      getView().notifyListAdapter();
      return;
    }
    Single.fromCallable(() -> {
      ArrayList<AppDetails> appDetailList = new ArrayList<>();
      if (packages == null) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager packageManager = getContext().getPackageManager();
        List<ResolveInfo> applicationInfos =
            packageManager.queryIntentActivities(mainIntent, PackageManager.GET_META_DATA);
        for (ResolveInfo resolveInfo : applicationInfos) {
          AppDetails appDetails = fetchDetail(resolveInfo.activityInfo.packageName);
          appDetailList.add(appDetails);
        }
      } else {
        for (String packageName : packages) {
          AppDetails appDetails = fetchDetail(packageName);
          appDetailList.add(appDetails);
        }
      }
      return appDetailList;
    })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<ArrayList<AppDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            getView().showProgressBar();
            getView().hideListView();
          }

          @Override public void onSuccess(@NonNull ArrayList<AppDetails> appDetails) {
            getView().hideProgressBar();
            getView().showListView();
            appDetailList = appDetails;
            getView().notifyListAdapter();
          }

          @Override public void onError(@NonNull Throwable e) {

          }
        });
  }

  private AppDetails fetchDetail(String packageName) {
    PackageManager packageManager = getContext().getPackageManager();
    AppDetails appDetails = new AppDetails();
    try {
      ApplicationInfo applicationInfo =
          packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
      appDetails.name = applicationInfo.loadLabel(packageManager).toString();
      appDetails.icon = packageManager.getApplicationIcon(packageName);
      appDetails.packageName = packageName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return appDetails;
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
}
