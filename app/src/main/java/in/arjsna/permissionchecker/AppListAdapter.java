package in.arjsna.permissionchecker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arjun on 7/6/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListViewHolder> {
  private final Context context;
  private final LayoutInflater layoutInflater;
  ArrayList<AppDetails> item = new ArrayList<>();

  public AppListAdapter(Context context) {
    this.context = context;
    this.layoutInflater = LayoutInflater.from(context);
  }

  @Override public AppListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AppListViewHolder(layoutInflater.inflate(R.layout.app_list_item, parent, false));
  }

  @Override public void onBindViewHolder(AppListViewHolder holder, int position) {
    final AppDetails appDetails = item.get(position);
    holder.appIcon.setImageDrawable(appDetails.icon);
    holder.appName.setText(appDetails.name);
    holder.itemView.setOnClickListener(v -> {
      Bundle bundle = new Bundle();
      bundle.putString("package_name", appDetails.packageName);
      AppDetailsFragment appDetailsFragment = new AppDetailsFragment();
      appDetailsFragment.setArguments(bundle);
      ((AppCompatActivity)context).getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.slide_in_right, R.anim.zoom_out, R.anim.zoom_in,
              R.anim.slide_out_right)
          .replace(R.id.permission_container, appDetailsFragment)
          .addToBackStack("appdetail")
          .commit();
    });
  }

  @Override public int getItemCount() {
    return item.size();
  }

  public void addAllAndNotify(List<AppDetails> appDetails) {
    item.addAll(appDetails);
    notifyDataSetChanged();
  }

  static class AppListViewHolder extends RecyclerView.ViewHolder {
    ImageView appIcon;
    TextView appName;
    public AppListViewHolder(View itemView) {
      super(itemView);
      appIcon = (ImageView) itemView.findViewById(R.id.app_icon_iv);
      appName = (TextView) itemView.findViewById(R.id.app_name_tv);
    }
  }
}
