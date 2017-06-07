package in.arjsna.permissionchecker;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by arjun on 7/6/17.
 */

public class AppListFragment extends Fragment {
  private View mRootView;
  private ArrayList<String> packages;
  private ArrayList<AppDetails> applications;
  private RecyclerView mAppListView;
  private AppListAdapter appListAdapter;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_list, container, false);
    mAppListView = (RecyclerView)mRootView.findViewById(R.id.app_list);
    mAppListView.setLayoutManager(new GridLayoutManager(getContext(), 4));
    appListAdapter = new AppListAdapter(getActivity());
    mAppListView.setAdapter(appListAdapter);
    setUpToolBar();
    packages = getArguments().getStringArrayList("packages");
    getAppDetails();
    return mRootView;
  }

  private void getAppDetails() {
    Single<List<AppDetails>> listSingle = Single.fromCallable(new Callable<List<AppDetails>>() {
      @Override public List<AppDetails> call() throws Exception {
        List<AppDetails> appDetailsList = new ArrayList<>();
        for (String packageName : packages) {
          AppDetails appDetails = fetchDetail(packageName);
          appDetailsList.add(appDetails);
        }
        return appDetailsList;
      }
    });
    listSingle.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<AppDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {

          }

          @Override public void onSuccess(@NonNull List<AppDetails> appDetails) {
            appListAdapter.addAllAndNotify(appDetails);
          }

          @Override public void onError(@NonNull Throwable e) {

          }
        });
  }

  private AppDetails fetchDetail(String packageName) {
    PackageManager packageManager = getActivity().getPackageManager();
    AppDetails appDetails = new AppDetails();
    try {
      ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
      appDetails.name = applicationInfo.loadLabel(packageManager).toString();
      appDetails.icon = packageManager.getApplicationIcon(packageName);
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return appDetails;
  }

  private void setUpToolBar() {
    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText("App List");
  }
}
