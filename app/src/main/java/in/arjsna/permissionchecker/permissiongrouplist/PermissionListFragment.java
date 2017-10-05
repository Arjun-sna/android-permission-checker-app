package in.arjsna.permissionchecker.permissiongrouplist;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import dagger.Lazy;
import in.arjsna.permissionchecker.applicationslist.AppListFragment;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.basemvp.BaseFragment;
import javax.inject.Inject;

/**
 * Created by arjun on 3/6/17.
 */

public class PermissionListFragment extends BaseFragment implements IPermissionGroupView {
  private View mRootView;
  private RecyclerView permissionsList;
  private ProgressBar pb;
  @Inject public PermissionGroupListAdapter permissionGroupListAdapter;
  @Inject public LinearLayoutManager linearLayoutManager;
  @Inject Lazy<IPermissionGroupPresenter<IPermissionGroupView>> permissionGroupPresenter;

  public PermissionListFragment() {
    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_permission_list, container, false);
    if (getFragmentComponent() != null) {
      getFragmentComponent().inject(this);
      permissionGroupPresenter.get().onAttach(this);
    }
    setUpToolBar();
    initViews();
    return mRootView;
  }

  private void initViews() {
    pb = mRootView.findViewById(R.id.permission_list_progress_bar);
    permissionsList = mRootView.findViewById(R.id.permission_list);
    permissionsList.setLayoutManager(linearLayoutManager);
    permissionsList.setAdapter(permissionGroupListAdapter);
    permissionGroupPresenter.get().onViewInitialised();
  }

  private void setUpToolBar() {
    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
    TextView titleTextView = toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText("Permission Groups");
    toolbar.setTitle("");
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
  }

  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.permissionlist_menu, menu);
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.listby:
        getActivity().getSupportFragmentManager()
            .beginTransaction()
            .setCustomAnimations(R.anim.slide_in_right, R.anim.zoom_out, R.anim.zoom_in,
                R.anim.slide_out_right)
            .replace(R.id.permission_container, new AppListFragment())
            .addToBackStack("App apps")
            .commit();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public void onDestroy() {
    super.onDestroy();
    permissionGroupPresenter.get().onDetach();
  }

  @Override public void showProgressBar() {
    pb.setVisibility(View.VISIBLE);
  }

  @Override public void hideProgressBar() {
    pb.setVisibility(View.GONE);
  }

  @Override public void showListView() {
    permissionsList.setVisibility(View.VISIBLE);
  }
  @Override public void hideListView() {
    permissionsList.setVisibility(View.GONE);
  }

  @Override public void notifyListAdapter() {
    permissionGroupListAdapter.notifyDataSetChanged();
  }
}
