package cn.hjf.csohelper;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.MyViewHolder> {
	private List<Uri> mDataset;



	// Provide a suitable constructor (depends on the kind of dataset)
	public PhotoListAdapter(List<Uri> myDataset) {
		mDataset = myDataset;
	}

	// Create new views (invoked by the layout manager)
	@Override
	public PhotoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
															int viewType) {
		// create a new view
		ImageView v = (ImageView) LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_photo, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	// Replace the contents of a view (invoked by the layout manager)
	@Override
	public void onBindViewHolder(MyViewHolder holder, int position) {
		// - get element from your dataset at this position
		// - replace the contents of the view with that element
		Glide.with(holder.mImageView)
				.load(mDataset.get(position))
				.into(holder.mImageView);
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
		public ImageView mImageView;

		public MyViewHolder(ImageView v) {
			super(v);
			mImageView = v;
		}
	}
}
