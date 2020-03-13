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

import cn.hjf.csohelper.model.CsoCompany;

public class CsoListAdapter extends RecyclerView.Adapter<CsoListAdapter.MyViewHolder> {
	private List<CsoCompany> mDataset;

	private Callback mCallback;

	private int mContextMenuPosition;

	// Provide a suitable constructor (depends on the kind of dataset)
	public CsoListAdapter(List<CsoCompany> myDataset) {
		mDataset = myDataset;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public CsoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
														  int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_cso_item, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(final MyViewHolder holder, final int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element

		holder.mTvItem.setText(mDataset.get(position).name);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onClick(position);
				}
			}
		});

		final Activity activity = (Activity)holder.itemView.getContext();
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
		return mDataset == null ? 0 : mDataset.size();
	}

	// Provide a reference to the views for each data item
	// Complex data items may need more than one view per item, and
	// you provide access to all the views for a data item in a view holder
	public static class MyViewHolder extends RecyclerView.ViewHolder {
		// each data item is just a string in this case
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
