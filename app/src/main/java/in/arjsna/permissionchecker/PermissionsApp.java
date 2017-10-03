package in.arjsna.permissionchecker;

import android.app.Application;
import in.arjsna.permissionchecker.di.components.DaggerApplicationComponent;
import in.arjsna.permissionchecker.di.modules.ApplicationModule;

public class PermissionsApp extends Application {
  private DaggerApplicationComponent applicationComponent;

  @Override public void onCreate() {
    super.onCreate();
    applicationComponent =
        (DaggerApplicationComponent) DaggerApplicationComponent.builder()
            .applicationModule(new ApplicationModule(this))
            .build();
  }

  public DaggerApplicationComponent getApplicationComponent() {
    return applicationComponent;
  }
}
