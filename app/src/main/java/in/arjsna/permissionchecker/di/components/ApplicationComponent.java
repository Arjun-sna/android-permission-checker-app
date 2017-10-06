package in.arjsna.permissionchecker.di.components;

import android.content.Context;
import dagger.Component;
import in.arjsna.permissionchecker.PermissionsApp;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.modules.ApplicationModule;
import in.arjsna.permissionchecker.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Singleton @Component(modules = ApplicationModule.class) public interface ApplicationComponent {
  void inject(PermissionsApp permissionsAppApp);

  @ApplicationContext Context getContext();

  DataProvider getDataProvider();
}
