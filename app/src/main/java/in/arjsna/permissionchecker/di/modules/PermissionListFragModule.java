package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.view.LayoutInflater;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.di.scopes.FragmentScope;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupPresenter;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupView;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionGroupPresenterImpl;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by arjun on 12/1/17.
 */

@Module
public class PermissionListFragModule {
  @Provides CompositeDisposable providesCompositeDisposable() {
    return new CompositeDisposable();
  }

  @FragmentScope
  @Provides IPermissionGroupPresenter<IPermissionGroupView> providesGroupPresenter(
      PermissionGroupPresenterImpl<IPermissionGroupView> permissionGroupPresenter) {
    return permissionGroupPresenter;
  }

  @Provides LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
    return LayoutInflater.from(context);
  }
}
