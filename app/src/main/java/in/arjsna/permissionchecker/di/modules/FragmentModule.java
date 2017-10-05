package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.support.transition.TransitionSet;
import android.support.v7.widget.LinearLayoutManager;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.Transition;
import in.arjsna.permissionchecker.appdetails.AppDetailsPresenterImpl;
import in.arjsna.permissionchecker.appdetails.IAppDetailsPresenter;
import in.arjsna.permissionchecker.appdetails.IAppDetailsView;
import in.arjsna.permissionchecker.applicationslist.AppListPresenterImpl;
import in.arjsna.permissionchecker.applicationslist.IAppListPresenter;
import in.arjsna.permissionchecker.applicationslist.IAppListView;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
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

  @FragmentScope @Provides IPermissionGroupPresenter<IPermissionGroupView> providesGroupPresenter(
      PermissionGroupPresenterImpl<IPermissionGroupView> permissionGroupPresenter) {
    return permissionGroupPresenter;
  }

  @FragmentScope @Provides IAppListPresenter<IAppListView> providedAppListPresenter(
      AppListPresenterImpl<IAppListView> appListPresenter) {
    return appListPresenter;
  }

  @FragmentScope @Provides IAppDetailsPresenter<IAppDetailsView> providedAppDetailsPresenter(
      AppDetailsPresenterImpl<IAppDetailsView> appDetailsPresenter) {
    return appDetailsPresenter;
  }
}
