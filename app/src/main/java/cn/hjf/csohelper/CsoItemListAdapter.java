package cn.hjf.csohelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cn.hjf.csohelper.model.CheckItem;

public class CsoItemListAdapter extends RecyclerView.Adapter<CsoItemListAdapter.MyViewHolder> {
	private List<CheckItem> mDataset;

	private Callback mCallback;

	// Provide a suitable constructor (depends on the kind of dataset)
	public CsoItemListAdapter(List<CheckItem> myDataset) {
		mDataset = myDataset;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public CsoItemListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
															  int viewType) {
		// create a new view
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_cso_item, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element

		holder.mTvItem.setText(mDataset.get(position).mName);

		holder.itemView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onClick(position);
				}
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
}
