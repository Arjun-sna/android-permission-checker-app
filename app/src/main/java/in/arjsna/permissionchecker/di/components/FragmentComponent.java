package in.arjsna.permissionchecker.di.components;

import dagger.Subcomponent;
import in.arjsna.permissionchecker.appdetails.AppDetailsFragment;
import in.arjsna.permissionchecker.applicationslist.AppListFragment;
import in.arjsna.permissionchecker.di.modules.FragmentModule;
import in.arjsna.permissionchecker.di.scopes.FragmentScope;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionListFragment;

@Subcomponent(modules = FragmentModule.class)
@FragmentScope
public interface FragmentComponent {
  void inject(PermissionListFragment permissionListFragment);

  void inject(AppListFragment appListFragment);

  void inject(AppDetailsFragment appDetailsFragment);
}
