package in.arjsna.permissionchecker.models;

public class PermissionDetail {
  public static final int VIEW_TYPE_ITEM = 0;
  public static final int VIEW_TYPE_SECTION = 1;
  public String permissionName;
  public String sectionName;
  public boolean isGranted;
  public int viewType;
}
