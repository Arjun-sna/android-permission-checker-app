package in.arjsna.permissionchecker;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by arjun on 4/6/17.
 */

class PermissionGroupDetails {
  public String permissionGroupName;
  public String permissionGroupDes;
  public int appsCount;
  public Set<String> appPackages = new HashSet<>();
}
