package in.arjsna.permissionchecker.di.modules;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import dagger.Module;
import dagger.Provides;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.di.scopes.ActivityScope;

@Module
public class ActivityModule {
  private AppCompatActivity appCompatActivity;

  public ActivityModule(AppCompatActivity appCompatActivity) {
    this.appCompatActivity = appCompatActivity;
  }

  @ActivityScope
  @ActivityContext
  @Provides
  Context providesContext() {
    return appCompatActivity;
  }

  @ActivityScope
  @Provides
  LinearLayoutManager providesLinearLayoutManager(@ActivityContext Context context) {
    return  new LinearLayoutManager(context);
  }

  @ActivityScope
  @Provides
  LayoutInflater provideLayoutInflater(@ActivityContext Context context) {
    return LayoutInflater.from(context);
  }
}
