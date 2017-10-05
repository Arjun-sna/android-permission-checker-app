package in.arjsna.permissionchecker.appdetails;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.Transition;
import in.arjsna.permissionchecker.basemvp.BaseFragment;
import javax.inject.Inject;

/**
 * Created by arjun on 7/6/17.
 */

public class AppDetailsFragment extends BaseFragment implements IAppDetailsView {
  private static final int UNINSTALL_APP_REQUEST = 500;
  private View mRootView;
  private ImageView appIcon;
  private TextView packageNameTv;
  private TextView appName;
  private RecyclerView permissionsList;
  private TextView noPermissionLabel;
  private TextView openAppBtn;
  private TextView appDetails;
  private String mPackageName;
  private TextView uninstall;

  @Inject public PermissionListAdapter permissionListAdapter;

  @Inject IAppDetailsPresenter<IAppDetailsView> appDetailsPresenter;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      setSharedElementEnterTransition(new Transition());
      setSharedElementReturnTransition(new Transition());
    }
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_details, container, false);
    if (getFragmentComponent() != null) {
      getFragmentComponent().inject(this);
      appDetailsPresenter.onAttach(this);
    }
    mPackageName = getArguments().getString("package_name");
    appDetailsPresenter.onIntentDataAvailable(mPackageName);
    initialiseViews();
    bindEvents();
    return mRootView;
  }

  private void bindEvents() {
    openAppBtn.setOnClickListener(v -> {
      Intent openAppIntent =
          getActivity().getPackageManager().getLaunchIntentForPackage(mPackageName);
      startActivity(openAppIntent);
    });
    appDetails.setOnClickListener(v -> {
      Intent intent = new Intent();
      intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      Uri uri = Uri.fromParts("package", mPackageName, null);
      intent.setData(uri);
      startActivity(intent);
    });
    RxView.clicks(uninstall).subscribe(o -> {
      Intent intent =
          new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", mPackageName, null));
      intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
      startActivityForResult(intent, UNINSTALL_APP_REQUEST);
    });
  }

  private void initialiseViews() {
    setUpToolBar();
    openAppBtn = mRootView.findViewById(R.id.open_app);
    appDetails = mRootView.findViewById(R.id.app_details);
    uninstall = mRootView.findViewById(R.id.app_uninstall);
    appIcon = mRootView.findViewById(R.id.app_picture);
    appName = mRootView.findViewById(R.id.app_name);
    noPermissionLabel = mRootView.findViewById(R.id.detail_label);
    packageNameTv = mRootView.findViewById(R.id.package_string);
    permissionsList = mRootView.findViewById(R.id.permission_list);
    permissionsList.setLayoutManager(new LinearLayoutManager(getActivity()));
    permissionsList.setAdapter(permissionListAdapter);
    appDetailsPresenter.onViewInitialised();
  }

  private void setUpToolBar() {
    Toolbar toolbar = getActivity().findViewById(R.id.toolbar);
    toolbar.setTitle("");
    TextView titleTextView = toolbar.findViewById(R.id.toolbar_title);
    titleTextView.setText("App Details");
    ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
    ActionBar supportActionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
    supportActionBar.setDisplayHomeAsUpEnabled(true);
    supportActionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white);
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == UNINSTALL_APP_REQUEST) {
      switch (resultCode) {
        case Activity.RESULT_OK:
          getActivity().onBackPressed();
          break;
        case Activity.RESULT_CANCELED:
          Toast.makeText(getContext(), "Cancelled.", Toast.LENGTH_LONG).show();
          break;
        default:
          Toast.makeText(getContext(), "Failed to uninstall the app.", Toast.LENGTH_LONG).show();
      }
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  @Override public void setAppIcon(Drawable icon) {
    appIcon.setImageDrawable(icon);
  }

  @Override public void setAppName(String name) {
    appName.setText(name);
  }

  @Override public void setPackageName(String packageName) {
    packageNameTv.setText(packageName);
  }

  @Override public void setLabelText(String text) {
    noPermissionLabel.setText(text);
  }

  @Override public void setPermissionCount(int count) {
    setLabelText(getString(R.string.permission_count, count));
  }

  @Override public void notifyAdapter() {
    permissionListAdapter.notifyDataSetChanged();
  }
}
