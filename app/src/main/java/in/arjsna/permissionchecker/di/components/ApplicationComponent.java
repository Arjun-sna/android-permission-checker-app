package in.arjsna.permissionchecker.di.components;

import android.app.Application;
import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import in.arjsna.permissionchecker.PermissionsApp;
import in.arjsna.permissionchecker.di.modules.MainActivityModule;
import in.arjsna.permissionchecker.di.modules.ApplicationModule;
import javax.inject.Singleton;

@Singleton
@Component(modules = { AndroidInjectionModule.class, ApplicationModule.class, ActivityBuilderModule.class})
public interface ApplicationComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance Builder application(Application application);

    ApplicationComponent build();
  }

  void inject(PermissionsApp permissionsApp);
}
