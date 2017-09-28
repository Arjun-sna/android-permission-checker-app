package in.arjsna.permissionchecker;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
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
  private List<AppDetails> appDetailList;
  private ProgressBar pb;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_list, container, false);
    pb = (ProgressBar) mRootView.findViewById(R.id.app_list_progress_bar);
    mAppListView = (RecyclerView) mRootView.findViewById(R.id.app_list);
    mAppListView.setLayoutManager(new GridLayoutManager(getContext(), 4));
    appListAdapter = new AppListAdapter(getActivity());
    mAppListView.setAdapter(appListAdapter);
    if (getArguments() != null) {
      packages = getArguments().getStringArrayList("packages");
    }
    setUpToolBar();
    getAppDetails();
    return mRootView;
  }

  private void getAppDetails() {
    Single<List<AppDetails>> listSingle = Single.fromCallable(() -> {
      if (appDetailList == null) {
        appDetailList = new ArrayList<>();
        if (packages == null) {
          Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
          mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
          PackageManager packageManager = getActivity().getPackageManager();
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
      }
      return appDetailList;
    });
    listSingle.subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new SingleObserver<List<AppDetails>>() {
          @Override public void onSubscribe(@NonNull Disposable d) {
            pb.setVisibility(View.VISIBLE);
            mAppListView.setVisibility(View.GONE);
          }

          @Override public void onSuccess(@NonNull List<AppDetails> appDetails) {
            pb.setVisibility(View.GONE);
            mAppListView.setVisibility(View.VISIBLE);
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

  private void setUpToolBar() {
    Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
    TextView titleTextView = (TextView) toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText(packages == null ? "All Installed Apps" : "App List");
    ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    supportActionBar.setDisplayHomeAsUpEnabled(true);
    supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
  }
}
