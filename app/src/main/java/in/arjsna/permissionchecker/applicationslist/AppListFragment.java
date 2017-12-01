package in.arjsna.permissionchecker.applicationslist;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.mindorks.nybus.NYBus;
import com.mindorks.nybus.annotation.Subscribe;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.appdetails.AppDetailsFragment;
import in.arjsna.permissionchecker.appdetails.AppUninstallEvent;
import in.arjsna.permissionchecker.basemvp.BaseFragment;
import in.arjsna.permissionchecker.models.AppDetails;
import javax.inject.Inject;

/**
 * Created by arjun on 7/6/17.
 */

public class AppListFragment extends BaseFragment implements IAppListView {
  private View mRootView;
  private RecyclerView mAppListView;

  private ProgressBar pb;
  @Inject public AppListAdapter appListAdapter;

  @Inject public IAppListPresenter<IAppListView> appListPresenter;
  private TextView titleTextView;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRetainInstance(true);
    NYBus.get().register(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_list, container, false);
    appListPresenter.onAttach(this);
    if (getArguments() != null) {
      appListPresenter.onIntentDataAvailable(getArguments().getStringArrayList("packages"));
    }
    initViews();
    return mRootView;
  }

  private void initViews() {
    pb = mRootView.findViewById(R.id.app_list_progress_bar);
    mAppListView = mRootView.findViewById(R.id.app_list);
    mAppListView.setLayoutManager(new GridLayoutManager(getContext(), 4));
    mAppListView.setAdapter(appListAdapter);
    setUpToolBar();
    appListPresenter.onViewInitialised();
  }

  private void setUpToolBar() {
    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
    toolbar.setTitle("");
    titleTextView = toolbar.findViewById(R.id.toolbar_title);
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    supportActionBar.setDisplayHomeAsUpEnabled(true);
    supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
  }

  @Override public void setTitle(String title) {
    titleTextView.setText(title);
  }

  @Override public void showProgressBar() {
    pb.setVisibility(View.VISIBLE);
  }

  @Override public void hideListView() {
    mAppListView.setVisibility(View.GONE);
  }

  @Override public void hideProgressBar() {
    pb.setVisibility(View.GONE);
  }

  @Override public void showListView() {
    mAppListView.setVisibility(View.VISIBLE);
  }

  @Override public void notifyListAdapter() {
    appListAdapter.notifyDataSetChanged();
  }

  @Override public void showFullDetails(AppDetails appDetails, int position) {
    getActivity().getSupportFragmentManager()
        .beginTransaction()
        .addSharedElement(
            ((AppListAdapter.AppListViewHolder) mAppListView.findViewHolderForAdapterPosition(
                position)).appIcon, "icon_transition")
        .setCustomAnimations(R.anim.zoom_in, R.anim.zoom_out, R.anim.zoom_in, R.anim.zoom_out)
        .replace(R.id.permission_container,
            AppDetailsFragment.getInstance(appDetails.packageName, position))
        .addToBackStack("appdetail")
        .commit();
  }

  @Subscribe public void onEvent(AppUninstallEvent event) {
    appListPresenter.onItemRemoved(event.positionOfAppInList);
  }

  @Override public void notifyItemRemoved(int positionOfAppInList) {
    appListAdapter.notifyItemRemoved(positionOfAppInList);
  }

  @Override public void onDestroy() {
    super.onDestroy();
    NYBus.get().unregister(this);
  }
}
