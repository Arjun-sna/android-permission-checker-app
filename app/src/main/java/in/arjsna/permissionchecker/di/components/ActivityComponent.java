package in.arjsna.permissionchecker.di.components;

import dagger.Component;
import in.arjsna.permissionchecker.MainActivity;
import in.arjsna.permissionchecker.di.modules.ActivityModule;
import in.arjsna.permissionchecker.di.scopes.ActivityScope;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionListFragment;

@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = ActivityModule.class)
public interface ActivityComponent {
  void inject(MainActivity mainActivity);

  void inject(PermissionListFragment permissionListFragment);
}
