package in.arjsna.permissionchecker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arjun on 4/6/17.
 */

public class PermissionListAdapter extends RecyclerView.Adapter<PermissionListAdapter.PermissionViewHolder> {
  private final Context context;
  private final ArrayList<PermissionDetails> list = new ArrayList<>();
  private final LayoutInflater layoutInflator;

  public PermissionListAdapter(Context context) {
    this.context = context;
    layoutInflator = LayoutInflater.from(context);
  }

  @Override public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView = layoutInflator.inflate(R.layout.permission_list_item, parent, false);
    return new PermissionViewHolder(itemView);
  }

  @Override public void onBindViewHolder(PermissionViewHolder holder, int position) {
    holder.permissionName.setText(list.get(position).permissionName);
  }

  @Override public int getItemCount() {
    return list.size();
  }

  public void addAll(List<PermissionDetails> strings) {
    list.addAll(strings);
    notifyDataSetChanged();
  }

  public static class PermissionViewHolder extends RecyclerView.ViewHolder {
    TextView permissionName;
    public PermissionViewHolder(View itemView) {
      super(itemView);
      permissionName = (TextView) itemView.findViewById(R.id.permission_group_name);
    }
  }
}
