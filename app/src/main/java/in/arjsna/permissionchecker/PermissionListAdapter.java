package in.arjsna.permissionchecker;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.permissionchecker.lib.TypeFaceTextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arjun on 4/6/17.
 */

public class PermissionListAdapter extends RecyclerView.Adapter<PermissionListAdapter.PermissionViewHolder> {
  private final Context context;
  private final ArrayList<PermissionGroupDetails> list = new ArrayList<>();
  private final LayoutInflater layoutInflater;

  public PermissionListAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = layoutInflater.inflate(R.layout.permission_list_item, parent, false);
    return new PermissionViewHolder(itemView);
  }

  @Override public void onBindViewHolder(final PermissionViewHolder holder, int position) {
    String[] permissionSplit = list.get(position).permissionGroupName.split("\\.");
    String permissionHeader= "";
    if (permissionSplit.length > 0) {
      permissionHeader = permissionSplit[permissionSplit.length - 1].replace("_", " ");
    }
    holder.permissionName.setText(permissionHeader);
    holder.permissionDes.setText(list.get(position).permissionGroupName);
    holder.appsCount.setText(String.valueOf(list.get(position).appsCount));
    holder.itemView.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("packages", new ArrayList<>(list.get(holder.getAdapterPosition()).appPackages));
        AppListFragment appListFragment = new AppListFragment();
        appListFragment.setArguments(bundle);
        ((AppCompatActivity)context).getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.permission_container, appListFragment)
            .addToBackStack("Permission Details")
            .commit();
      }
    });
  }

  @Override public int getItemCount() {
    return list.size();
  }

  void addAll(List<PermissionGroupDetails> strings) {
    list.addAll(strings);
    notifyDataSetChanged();
  }

  static class PermissionViewHolder extends RecyclerView.ViewHolder {
    TextView permissionName;
    TextView permissionDes;
    TextView appsCount;
    public PermissionViewHolder(View itemView) {
      super(itemView);
      permissionName = (TextView) itemView.findViewById(R.id.permission_group_name);
      permissionDes = (TextView) itemView.findViewById(R.id.permission_group_description);
      appsCount = (TextView) itemView.findViewById(R.id.permission_group_app_count);
    }
  }
}
