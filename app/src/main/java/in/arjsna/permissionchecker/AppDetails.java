package in.arjsna.permissionchecker;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;

/**
 * Created by arjun on 7/6/17.
 */

class AppDetails {
  public String name;
  public Drawable icon;
  public String packageName;
  public ArrayList<String> grantedPermissionList = new ArrayList<>();
  public ArrayList<String> deniedPermissionList = new ArrayList<>();
}
