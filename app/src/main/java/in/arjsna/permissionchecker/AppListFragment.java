package in.arjsna.permissionchecker;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by arjun on 7/6/17.
 */

public class AppListFragment extends Fragment {
  private View mRootView;

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    mRootView = inflater.inflate(R.layout.fragment_app_list, container, false);
    return mRootView;
  }
}
