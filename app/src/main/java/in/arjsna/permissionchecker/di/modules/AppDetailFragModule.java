package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.view.LayoutInflater;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.appdetails.AppDetailsPresenterImpl;
import in.arjsna.permissionchecker.appdetails.IAppDetailsPresenter;
import in.arjsna.permissionchecker.appdetails.IAppDetailsView;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.di.scopes.FragmentScope;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by arjun on 12/1/17.
 */

@Module
public class AppDetailFragModule {
  @Provides CompositeDisposable providesCompositeDisposable() {
    return new CompositeDisposable();
  }

  @FragmentScope
  @Provides IAppDetailsPresenter<IAppDetailsView> providedAppDetailsPresenter(
      AppDetailsPresenterImpl<IAppDetailsView> appDetailsPresenter) {
    return appDetailsPresenter;
  }

  @Provides LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
    return LayoutInflater.from(context);
  }
}
