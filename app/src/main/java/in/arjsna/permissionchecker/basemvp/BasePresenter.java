package in.arjsna.permissionchecker.basemvp;

import android.content.Context;
import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends IMVPView> implements IMVPPresenter<V> {
  private V view;
  private final CompositeDisposable compositeDisposable;
  private final Context context;

  protected BasePresenter(Context context, CompositeDisposable compositeDisposable) {
    this.compositeDisposable = compositeDisposable;
    this.context = context;
  }

  @Override public void onAttach(V view) {
    this.view = view;
  }

  @Override public void onDetach() {
    compositeDisposable.dispose();
    view = null;
  }

  protected V getView() {
    return view;
  }

  public CompositeDisposable getCompositeDisposable() {
    return compositeDisposable;
  }

  protected Context getContext() {
    return context;
  }
}
