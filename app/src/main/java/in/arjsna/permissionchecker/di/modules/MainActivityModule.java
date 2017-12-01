package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.view.LayoutInflater;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.MainActivity;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;

@Module public class MainActivityModule {

  @ActivityContext
  @Provides
  Context providesContext(MainActivity activity) {
    return activity;
  }
}
