package in.arjsna.permissionchecker.permissiongrouplist;

import in.arjsna.permissionchecker.R;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by arjun on 7/6/17.
 */

public class ResourceMap {
  public static Map<String, Integer> resourceMap = new HashMap<String, Integer>() {{
    put("MICROPHONE", R.drawable.ic_mic);
    put("LOCATION", R.drawable.ic_location);
    put("PERSONAL INFO", R.drawable.ic_info);
    put("CONTACTS", R.drawable.ic_contacts);
    put("SMS", R.drawable.ic_sms);
    put("STORAGE", R.drawable.ic_sd_storage);
    put("ACCOUNTS", R.drawable.ic_account);
    put("CAMERA", R.drawable.ic_photo_camera);
    put("CALENDAR", R.drawable.ic_date);
    put("SENSORS", R.drawable.sensor);
    put("NETWORK", R.drawable.ic_network);
    put("MESSAGES", R.drawable.ic_attach_file);
    put("PHONE", R.drawable.ic_phone);
  }};
}
