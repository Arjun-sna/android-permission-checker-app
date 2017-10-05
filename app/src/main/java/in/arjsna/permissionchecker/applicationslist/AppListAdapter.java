package in.arjsna.permissionchecker.applicationslist;

import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import in.arjsna.permissionchecker.R;
import in.arjsna.permissionchecker.models.AppDetails;
import javax.inject.Inject;

/**
 * Created by arjun on 7/6/17.
 */

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppListViewHolder> {
  private final LayoutInflater layoutInflater;
  private final IAppListPresenter<IAppListView> appListPresenter;

  @Inject public AppListAdapter(IAppListPresenter<IAppListView> appListPresenter,
      LayoutInflater layoutInflater) {
    this.appListPresenter = appListPresenter;
    this.layoutInflater = layoutInflater;
  }

  @Override public AppListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new AppListViewHolder(layoutInflater.inflate(R.layout.app_list_item, parent, false),
        appListPresenter);
  }

  @Override public void onBindViewHolder(AppListViewHolder holder, int position) {
    final AppDetails appDetails = appListPresenter.getItemAt(position);
    holder.appIcon.setImageDrawable(appDetails.icon);
    holder.appName.setText(appDetails.name);
    ViewCompat.setTransitionName(holder.appIcon, String.valueOf(position) + "_icon");
  }

  @Override public int getItemCount() {
    return appListPresenter.getItemCount();
  }

  static class AppListViewHolder extends RecyclerView.ViewHolder {
    private final IAppListPresenter<IAppListView> appListPresenter;
    final ImageView appIcon;
    final TextView appName;

    public AppListViewHolder(View itemView, IAppListPresenter<IAppListView> appListPresenter) {
      super(itemView);
      appIcon = itemView.findViewById(R.id.app_icon_iv);
      appName = itemView.findViewById(R.id.app_name_tv);
      this.appListPresenter = appListPresenter;
      bindEvents();
    }

    private void bindEvents() {
      itemView.setOnClickListener(v -> appListPresenter.onListItemClicked(getAdapterPosition()));
    }
  }
}
