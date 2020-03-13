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

import cn.hjf.csohelper.data.model.Check;

public class CheckListAdapter extends RecyclerView.Adapter<CheckListAdapter.MyViewHolder> {

	private List<Check> mCheckList;
	private Map<String, Integer> mMap;
	private Callback mCallback;
	private int mContextMenuPosition;

	public CheckListAdapter(List<Check> checkList, Map<String, Integer> map) {
		mCheckList = checkList;
		mMap = map;
	}

	@Override
	public CheckListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
															int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_cso_check_item, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		Check check = mCheckList.get(position);
		holder.mTvItem.setText(check.mName);
		holder.mTvCount.setText(String.valueOf(mMap.get(check.mName)));

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
				inflater.inflate(R.menu.menu_list_context, menu);
			}
		});
	}

	@Override
	public int getItemCount() {
		return mCheckList == null ? 0 : mCheckList.size();
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
