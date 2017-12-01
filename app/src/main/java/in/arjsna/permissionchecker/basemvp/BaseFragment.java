package in.arjsna.permissionchecker.basemvp;

import android.content.Context;
import android.support.v4.app.Fragment;
import dagger.android.support.AndroidSupportInjection;

public abstract class BaseFragment extends Fragment implements IMVPView {

  @Override public void onAttach(Context context) {
    AndroidSupportInjection.inject(this);
    super.onAttach(context);
  }
}
