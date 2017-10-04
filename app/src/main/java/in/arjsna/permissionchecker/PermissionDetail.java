package in.arjsna.permissionchecker;

public class PermissionDetail {
  public static final int VIEW_TYPE_ITEM = 0;
  public static final int VIEW_TYPE_SECTION = 1;
  String permissionName;
  String sectionName;
  boolean isGranted;
  int viewType;
}
