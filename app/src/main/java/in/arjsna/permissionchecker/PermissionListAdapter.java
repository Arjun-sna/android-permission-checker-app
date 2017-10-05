package in.arjsna.permissionchecker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.permissionchecker.models.PermissionDetail;
import java.util.ArrayList;

/**
 * Created by arjun on 7/6/17.
 */

public class PermissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final LayoutInflater layoutInflater;
  private ArrayList<PermissionDetail> permissions = new ArrayList<>();

  public PermissionListAdapter(Context context) {
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == PermissionDetail.VIEW_TYPE_ITEM) {
      return new PermissionListViewHolder(
          layoutInflater.inflate(R.layout.permission_list_item, parent, false));
    } else {
      return new SectionViewHolder(
          layoutInflater.inflate(R.layout.permission_section_view, parent, false));
    }
  }

  public void addAllAndNotify(ArrayList<PermissionDetail> permissions) {
    this.permissions.addAll(permissions);
    notifyDataSetChanged();
  }

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PermissionDetail permissionDetail = permissions.get(position);
    if (permissionDetail.viewType == PermissionDetail.VIEW_TYPE_ITEM) {
      ((PermissionListViewHolder) holder).permissionId.setText(permissionDetail.permissionName);
    } else {
      ((SectionViewHolder) holder).sectionName.setText(permissionDetail.sectionName);
    }
  }

  @Override public int getItemCount() {
    return permissions.size();
  }

  @Override public int getItemViewType(int position) {
    return permissions.get(position).viewType;
  }

  static class PermissionListViewHolder extends RecyclerView.ViewHolder {
    TextView permissionId;

    PermissionListViewHolder(View itemView) {
      super(itemView);
      permissionId = itemView.findViewById(R.id.permission_id);
    }
  }

  static class SectionViewHolder extends RecyclerView.ViewHolder {
    TextView sectionName;

    SectionViewHolder(View itemView) {
      super(itemView);
      sectionName = itemView.findViewById(R.id.section_name_view);
    }
  }
}
