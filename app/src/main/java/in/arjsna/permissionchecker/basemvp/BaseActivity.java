package in.arjsna.permissionchecker.basemvp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import in.arjsna.permissionchecker.PermissionsApp;
import in.arjsna.permissionchecker.di.components.DaggerActivityComponent;

public abstract class BaseActivity extends AppCompatActivity implements IMVPView {
  private DaggerActivityComponent activityComponent;

  @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityComponent = (DaggerActivityComponent) DaggerActivityComponent.builder()
        .applicationComponent(((PermissionsApp) getApplication()).getApplicationComponent())
        .build();
  }

  public DaggerActivityComponent getActivityComponent() {
    return activityComponent;
  }
}
