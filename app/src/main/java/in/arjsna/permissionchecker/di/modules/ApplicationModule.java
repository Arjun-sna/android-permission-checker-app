package in.arjsna.permissionchecker.di.modules;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Module public class ApplicationModule {
  private final Application application;

  public ApplicationModule(Application application) {
    this.application = application;
  }

  @Provides @Singleton @ApplicationContext Context provideApplicationContext() {
    return application.getApplicationContext();
  }

  @Provides @Singleton DataProvider provideDataProvider(@ApplicationContext Context context) {
    return new DataProvider(context);
  }
}
