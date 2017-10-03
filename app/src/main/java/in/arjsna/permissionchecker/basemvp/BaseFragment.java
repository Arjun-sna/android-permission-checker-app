package in.arjsna.permissionchecker.basemvp;

import android.content.Context;
import android.support.v4.app.Fragment;

public abstract class BaseFragment extends Fragment implements IMVPView {
  private BaseActivity baseActivity;

  @Override public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof BaseActivity) {
      this.baseActivity = (BaseActivity) context;
    }
  }
}