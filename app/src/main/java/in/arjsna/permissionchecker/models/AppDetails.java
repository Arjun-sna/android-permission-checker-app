package in.arjsna.permissionchecker.models;

import android.graphics.drawable.Drawable;
import java.util.ArrayList;

/**
 * Created by arjun on 7/6/17.
 */

public class AppDetails {
  public String name;
  public Drawable icon;
  public String packageName;
  public ArrayList<String> grantedPermissionList = new ArrayList<>();
  public ArrayList<String> deniedPermissionList = new ArrayList<>();
}
