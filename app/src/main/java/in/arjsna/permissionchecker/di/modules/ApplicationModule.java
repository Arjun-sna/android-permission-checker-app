package in.arjsna.permissionchecker.di.modules;

import android.app.Application;
import android.content.Context;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.datamanager.DataProvider;
import in.arjsna.permissionchecker.di.qualifiers.ApplicationContext;
import javax.inject.Singleton;

@Module public class ApplicationModule {
  @Provides @Singleton @ApplicationContext Context provideApplicationContext(
      Application application) {
    return application.getApplicationContext();
  }

  @Provides @Singleton DataProvider provideDataProvider(@ApplicationContext Context context) {
    return new DataProvider(context);
  }
}
