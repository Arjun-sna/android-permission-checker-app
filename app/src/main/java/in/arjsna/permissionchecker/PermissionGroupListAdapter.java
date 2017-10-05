package in.arjsna.permissionchecker;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.permissionchecker.di.qualifiers.ActivityContext;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupPresenter;
import in.arjsna.permissionchecker.permissiongrouplist.IPermissionGroupView;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/**
 * Created by arjun on 4/6/17.
 */

public class PermissionGroupListAdapter
    extends RecyclerView.Adapter<PermissionGroupListAdapter.PermissionViewHolder> {
  private final Context context;
  private final LayoutInflater layoutInflater;
  private final IPermissionGroupPresenter<IPermissionGroupView> permissionGroupPresenter;

  @Inject
  public PermissionGroupListAdapter(@ActivityContext Context context, LayoutInflater layoutInflater,
      IPermissionGroupPresenter<IPermissionGroupView> permissionGroupPresenter) {
    this.context = context;
    this.layoutInflater = layoutInflater;
    this.permissionGroupPresenter = permissionGroupPresenter;
  }

  @Override public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = layoutInflater.inflate(R.layout.permission_group_list_item, parent, false);
    return new PermissionViewHolder(itemView);
  }

  @Override public void onBindViewHolder(final PermissionViewHolder holder, int position) {
    String[] permissionSplit = permissionGroupPresenter.getItemAt(position).permissionGroupName.split("\\.");
    String permissionHeader = "";
    if (permissionSplit.length > 0) {
      permissionHeader = permissionSplit[permissionSplit.length - 1].replace("_", " ");
    }
    holder.permissionName.setText(permissionHeader);
    setDrawable(holder, ResourceMap.resourceMap.get(permissionHeader));
    holder.permissionDes.setText(permissionGroupPresenter.getItemAt(position).permissionGroupDes);
    holder.appsCount.setText(String.valueOf(permissionGroupPresenter.getItemAt(position).appsCount));
    holder.itemView.setOnClickListener(v -> {
      Bundle bundle = new Bundle();
      bundle.putStringArrayList("packages",
          new ArrayList<>(permissionGroupPresenter.getItemAt(holder.getAdapterPosition()).appPackages));
      AppListFragment appListFragment = new AppListFragment();
      appListFragment.setArguments(bundle);
      ((AppCompatActivity) context).getSupportFragmentManager()
          .beginTransaction()
          .setCustomAnimations(R.anim.slide_in_right, R.anim.zoom_out, R.anim.zoom_in,
              R.anim.slide_out_right)
          .replace(R.id.permission_container, appListFragment)
          .addToBackStack("Permission Details")
          .commit();
    });
  }

  private void setDrawable(PermissionViewHolder holder, Integer id) {
    if (id == null) {
      id = R.drawable.ic_android;
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
      holder.permissionName.setCompoundDrawablesRelativeWithIntrinsicBounds(id, 0, 0, 0);
    } else {
      holder.permissionName.setCompoundDrawables(context.getResources().getDrawable(id), null, null,
          null);
    }
  }

  @Override public int getItemCount() {
    return permissionGroupPresenter.getItemCount();
  }

  static class PermissionViewHolder extends RecyclerView.ViewHolder {
    TextView permissionName;
    TextView permissionDes;
    TextView appsCount;

    public PermissionViewHolder(View itemView) {
      super(itemView);
      permissionName = itemView.findViewById(R.id.permission_group_name);
      permissionDes = itemView.findViewById(R.id.permission_group_description);
      appsCount = itemView.findViewById(R.id.permission_group_app_count);
    }
  }
}
