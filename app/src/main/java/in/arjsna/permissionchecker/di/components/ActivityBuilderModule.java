package in.arjsna.permissionchecker.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.permissionchecker.MainActivity;
import in.arjsna.permissionchecker.di.modules.MainActivityModule;

/**
 * Created by arjun on 12/1/17.
 */
@Module
public abstract class ActivityBuilderModule {
  @ContributesAndroidInjector(modules = { MainActivityModule.class, FragmentBuilderModule.class })
  abstract MainActivity contributeMainActivity();
}
