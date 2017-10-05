package in.arjsna.permissionchecker.basemvp;

import android.content.Context;
import android.support.v4.app.Fragment;
import in.arjsna.permissionchecker.di.components.FragmentComponent;

public abstract class BaseFragment extends Fragment implements IMVPView {
  private FragmentComponent fragmentComponent;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof BaseActivity) {
      BaseActivity baseActivity = (BaseActivity) context;
      fragmentComponent = baseActivity.getActivityComponent().plusFragmentComponent();
    }
  }

  protected FragmentComponent getFragmentComponent() {
    return fragmentComponent;
  }
}
