package in.arjsna.permissionchecker.appdetails;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.jakewharton.rxbinding2.view.RxView;
import com.mindorks.nybus.NYBus;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.Transition;
import in.arjsna.permissionchecker.basemvp.BaseFragment;
import javax.inject.Inject;

/**
 * Created by arjun on 7/6/17.
 */

public class AppDetailsFragment extends BaseFragment implements IAppDetailsView {
  private static final int UNINSTALL_APP_REQUEST = 500;
  private static final int APP_SETTINGS_REQUEST = 501;
  private static final int STORAGE_PERMISSION = 100;
  private View mRootView;
  private ImageView appIcon;
  private TextView packageNameTv;
  private TextView appName;
  private TextView noPermissionLabel;
  private TextView openAppBtn;
  private TextView appDetails;
  private String mPackageName;
  private TextView uninstall;
  private TextView extractBtn;

  @Inject public PermissionListAdapter permissionListAdapter;
  @Inject IAppDetailsPresenter<IAppDetailsView> appDetailsPresenter;
  private int mPositionInList;

  public static AppDetailsFragment getInstance(String packageName, int position) {
    Bundle bundle = new Bundle();
    bundle.putString("package_name", packageName);
    bundle.putInt("item_position", position);
    AppDetailsFragment appDetailsFragment = new AppDetailsFragment();
    appDetailsFragment.setArguments(bundle);
    return appDetailsFragment;
  }

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
    mPositionInList = getArguments().getInt("item_position");
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
      startActivityForResult(intent, APP_SETTINGS_REQUEST);
    });
    RxView.clicks(uninstall).subscribe(o -> {
      Intent intent =
          new Intent(Intent.ACTION_DELETE, Uri.fromParts("package", mPackageName, null));
      intent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
      startActivityForResult(intent, UNINSTALL_APP_REQUEST);
    });
    RxView.clicks(extractBtn).subscribe(o -> {
      appDetailsPresenter.extractAndSaveApk();
    });
  }

  private void initialiseViews() {
    setUpToolBar();
    openAppBtn = mRootView.findViewById(R.id.open_app);
    appDetails = mRootView.findViewById(R.id.app_details);
    uninstall = mRootView.findViewById(R.id.app_uninstall);
    appIcon = mRootView.findViewById(R.id.app_picture);
    appName = mRootView.findViewById(R.id.app_name);
    extractBtn = mRootView.findViewById(R.id.extract_apk);
    noPermissionLabel = mRootView.findViewById(R.id.detail_label);
    packageNameTv = mRootView.findViewById(R.id.package_string);
    RecyclerView permissionsList = mRootView.findViewById(R.id.permission_list);
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
          appDetailsPresenter.onDataChanged();
          NYBus.get().post(new AppUninstallEvent(mPositionInList));
          getActivity().onBackPressed();
          break;
        case Activity.RESULT_CANCELED:
          Toast.makeText(getContext(), "Cancelled.", Toast.LENGTH_LONG).show();
          break;
        default:
          Toast.makeText(getContext(), "Failed to uninstall the app.", Toast.LENGTH_LONG).show();
      }
      return;
    } else if (requestCode == APP_SETTINGS_REQUEST) {
      Log.i("Debug", " result");
      appDetailsPresenter.onSettingsChanged(mPackageName);
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

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    if (requestCode != STORAGE_PERMISSION) {
      return;
    }

    if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      appDetailsPresenter.onPermissionGranted();
      return;
    }
    if (shouldShowRequestPermissionRationale(permissions[0])) {
      showRationale();
      return;
    }
    appDetailsPresenter.onPermissionDenied();
  }

  private void showRationale() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
        .setTitle(R.string.permission_denied)
        .setMessage(R.string.storage_permission_requirement)
        .setPositiveButton(R.string.done, (dialog, which) -> {
          requestForStoragePermission();
          dialog.dismiss();
        })
        .setNegativeButton(R.string.cancel, (dialog, which) -> {
          appDetailsPresenter.onPermissionDenied();
          dialog.dismiss();
        });
    builder.show();
  }

  public void requestForStoragePermission() {
    if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
      appDetailsPresenter.onPermissionGranted();
    } else {
      final String[] permissions = new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE};
      requestPermissions(permissions, STORAGE_PERMISSION);
    }
  }

  @Override public void onExtractionComplete(String path) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
        .setTitle(R.string.extraction_completed)
        .setMessage(R.string.prompt_for_opening_folder)
        .setPositiveButton(R.string.open_folder, (dialog, which) -> {
          Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
          intent.setDataAndType(
              Uri.parse(Environment.getExternalStorageDirectory() + "/AppPermissionsExtractedApk"),
              "*/*");
          startActivity(Intent.createChooser(intent, "Open folder"));
        })
        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
    builder.show();
  }

  @Override public void showError(String errorMsg) {
    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_LONG).show();
  }

  @Override public void showFileExitsAlert() {
    AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
        .setTitle(R.string.file_exits)
        .setMessage(R.string.file_exits_detail)
        .setPositiveButton(R.string.replace,
            (dialog, which) -> appDetailsPresenter.extractByReplacing())
        .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss());
    builder.show();
  }
}
