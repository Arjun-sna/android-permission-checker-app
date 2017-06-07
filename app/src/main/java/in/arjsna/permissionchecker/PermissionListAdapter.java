package in.arjsna.permissionchecker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by arjun on 7/6/17.
 */

public class PermissionListAdapter extends RecyclerView.Adapter<PermissionListAdapter.PermissionListViewHolder> {
  private final Context context;
  private final LayoutInflater layoutInflater;
  ArrayList<String> permissions = new ArrayList<>();

  public PermissionListAdapter(Context context) {
    this.context = context;
    layoutInflater = LayoutInflater.from(context);
  }

  @Override public PermissionListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new PermissionListViewHolder(layoutInflater.inflate(R.layout.permission_list_item, parent, false));
  }

  public void addAllAndNotify(ArrayList<String> permissions) {
    this.permissions.addAll(permissions);
    notifyDataSetChanged();
  }

  @Override public void onBindViewHolder(PermissionListViewHolder holder, int position) {
    holder.permissionId.setText(permissions.get(position));
  }

  @Override public int getItemCount() {
    return permissions.size();
  }

  static class PermissionListViewHolder extends RecyclerView.ViewHolder {
    TextView permissionId;
    public PermissionListViewHolder(View itemView) {
      super(itemView);
      permissionId = (TextView) itemView.findViewById(R.id.permission_id);
    }
  }
}
