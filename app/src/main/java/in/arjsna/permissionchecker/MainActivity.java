package in.arjsna.permissionchecker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.MenuItem;
import com.github.fernandodev.easyratingdialog.library.EasyRatingDialog;
import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import in.arjsna.permissionchecker.basemvp.BaseActivity;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionListFragment;
import javax.inject.Inject;

public class MainActivity extends BaseActivity implements HasSupportFragmentInjector {

  private EasyRatingDialog ratingDialog;

  @Inject DispatchingAndroidInjector<Fragment> androidInjector;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    if (savedInstanceState == null) {
      getSupportFragmentManager().beginTransaction()
          .add(R.id.permission_container, new PermissionListFragment())
          .commit();
    }
    ratingDialog = new EasyRatingDialog(this);
  }

  @Override protected void onStart() {
    super.onStart();
    ratingDialog.onStart();
  }

  @Override
  protected void onResume() {
    super.onResume();
    ratingDialog.showIfNeeded();
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override public AndroidInjector<Fragment> supportFragmentInjector() {
    return androidInjector;
  }
}
