package in.arjsna.permissionchecker.basemvp;

import android.content.Context;
import io.reactivex.disposables.CompositeDisposable;

public class BasePresenter<V extends IMVPView> implements IMVPPresenter<V> {
  private V view;
  private CompositeDisposable compositeDisposable;
  private Context context;

  public BasePresenter(Context context, CompositeDisposable compositeDisposable) {
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

  public V getView() {
    return view;
  }

  public CompositeDisposable getCompositeDisposable() {
    return compositeDisposable;
  }

  public Context getContext() {
    return context;
  }
}
