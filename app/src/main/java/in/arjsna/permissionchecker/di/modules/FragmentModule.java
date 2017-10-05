package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.di.scopes.ActivityScope;
import in.arjsna.permissionchecker.di.scopes.FragmentScope;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupPresenter;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupView;
import in.arjsna.permissionchecker.permissiongrouplist.PermissionGroupPresenterImpl;
import io.reactivex.disposables.CompositeDisposable;

@Module public class FragmentModule {
  @Provides LinearLayoutManager providesLinearLayoutManager(@ActivityContext Context context) {
    return new LinearLayoutManager(context);
  }

  @FragmentScope @Provides CompositeDisposable providesCompositeDisposable() {
    return new CompositeDisposable();
  }

  @FragmentScope @Provides
  IPermissionGroupPresenter<IPermissionGroupView> providesGroupPresenterLazy(
      PermissionGroupPresenterImpl<IPermissionGroupView> permissionGroupPresenter) {
    return permissionGroupPresenter;
  }

}
