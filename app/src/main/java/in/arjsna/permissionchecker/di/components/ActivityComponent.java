package in.arjsna.permissionchecker.di.components;

import dagger.Component;
import in.arjsna.permissionchecker.di.modules.ActivityModule;
import in.arjsna.permissionchecker.di.scopes.ActivityScope;

@ActivityScope
@Component(modules = ActivityModule.class, dependencies = ApplicationComponent.class)
public interface ActivityComponent {
}
