package cn.hjf.csohelper;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class CsoListAdapter extends RecyclerView.Adapter<CsoListAdapter.MyViewHolder> {

	private List<String> mList;
	private Map<String, String> mMap;
	private Callback mCallback;
	private int mContextMenuPosition;

	public CsoListAdapter(List<String> list, Map<String, String> map) {
		mList = list;
		mMap = map;
	}

	@Override
	public CsoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_cso_item, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {
		String cso = mList.get(position);
		holder.mTvItem.setText(cso);
		holder.mTvCount.setText(mMap.get(cso));

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onClick(position);
				}
			}
		});

		final Activity activity = (Activity) holder.itemView.getContext();
		activity.registerForContextMenu(holder.itemView);
		holder.itemView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
				mContextMenuPosition = position;
				MenuInflater inflater = activity.getMenuInflater();
				inflater.inflate(R.menu.menu_list, menu);
			}
		});
	}

	// Return the size of your dataset (invoked by the layout manager)
	@Override
	public int getItemCount() {
		return mList == null ? 0 : mList.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		public TextView mTvItem;
		public TextView mTvCount;

		public MyViewHolder(View root) {
			super(root);
			mTvItem = root.findViewById(R.id.tv_item);
			mTvCount = root.findViewById(R.id.tv_count);
		}
	}

	public interface Callback {
		void onClick(int position);
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public int getContextMenuPosition() {
		return mContextMenuPosition;
	}
}
