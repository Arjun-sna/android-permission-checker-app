package in.arjsna.permissionchecker.di.components;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import in.arjsna.permissionchecker.appdetails.AppDetailsFragment;
import in.arjsna.permissionchecker.applicationslist.AppListFragment;
import in.arjsna.permissionchecker.di.modules.AppDetailFragModule;
import in.arjsna.permissionchecker.di.modules.AppListFragModule;
import in.arjsna.permissionchecker.di.modules.PermissionListFragModule;
import in.arjsna.permissionchecker.di.scopes.FragmentScope;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionListFragment;

/**
 * Created by arjun on 12/1/17.
 */

@Module
abstract class FragmentBuilderModule {
  @FragmentScope
  @ContributesAndroidInjector(modules = { PermissionListFragModule.class })
  abstract PermissionListFragment contributePermissionListFragment();

  @FragmentScope
  @ContributesAndroidInjector(modules = { AppListFragModule.class })
  abstract AppListFragment contributeAppListFragment();

  @FragmentScope
  @ContributesAndroidInjector(modules = { AppDetailFragModule.class })
  abstract AppDetailsFragment contributeAppDetailsFragment();
}
