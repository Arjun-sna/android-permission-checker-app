package in.arjsna.permissionchecker.basemvp;

public interface IMVPPresenter<V extends IMVPView> {
  void onAttach(V view);

  void onDetach();
}
