package in.arjsna.permissionchecker.models;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by arjun on 4/6/17.
 */

public class PermissionGroupDetails {
  public String permissionGroupName;
  public String permissionGroupDes;
  public int appsCount;
  public final Set<String> appPackages = new HashSet<>();
}
