package in.arjsna.permissionchecker.basemvp;

import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends IMVPView> implements IMVPPresenter<V> {
  private V view;
  private CompositeDisposable compositeDisposable;

  @Override public void onAttach(V view) {
    this.view = view;
  }

  @Override public void onDetach() {
    compositeDisposable.dispose();
    view = null;
  }

  public V getView() {
    return view;
  }

  public CompositeDisposable getCompositeDisposable() {
    return compositeDisposable;
  }
}
