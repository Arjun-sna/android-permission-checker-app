package in.arjsna.permissionchecker.appdetails;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.models.PermissionDetail;
import javax.inject.Inject;

/**
 * Created by arjun on 7/6/17.
 */

public class PermissionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
  private final LayoutInflater layoutInflater;
  private final IAppDetailsPresenter<IAppDetailsView> appDetailsPresenter;

  @Inject public PermissionListAdapter(LayoutInflater layoutInflater,
      IAppDetailsPresenter<IAppDetailsView> appDetailsPresenter) {
    this.layoutInflater = layoutInflater;
    this.appDetailsPresenter = appDetailsPresenter;
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

  @Override public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    PermissionDetail permissionDetail = appDetailsPresenter.getItemAt(position);
    if (permissionDetail.viewType == PermissionDetail.VIEW_TYPE_ITEM) {
      ((PermissionListViewHolder) holder).permissionId.setText(permissionDetail.permissionName);
    } else {
      ((SectionViewHolder) holder).sectionName.setText(permissionDetail.sectionName);
    }
  }

  @Override public int getItemCount() {
    return appDetailsPresenter.getItemCount();
  }

  @Override public int getItemViewType(int position) {
    return appDetailsPresenter.getItemAt(position).viewType;
  }

  static class PermissionListViewHolder extends RecyclerView.ViewHolder {
    final TextView permissionId;

    PermissionListViewHolder(View itemView) {
      super(itemView);
      permissionId = itemView.findViewById(R.id.permission_id);
    }
  }

  static class SectionViewHolder extends RecyclerView.ViewHolder {
    final TextView sectionName;

    SectionViewHolder(View itemView) {
      super(itemView);
      sectionName = itemView.findViewById(R.id.section_name_view);
    }
  }
}
