package cn.hjf.csohelper;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.hjf.csohelper.data.model.Photo;

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.MyViewHolder> {

	private List<Photo> mPhotoList;
	private Callback mCallback;

	public PhotoListAdapter(List<Photo> list) {
		mPhotoList = list;
	}

	@Override
	public PhotoListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
															int viewType) {
		View v = LayoutInflater.from(parent.getContext())
				.inflate(R.layout.view_photo, parent, false);
		MyViewHolder vh = new MyViewHolder(v);
		return vh;
	}

	@Override
	public void onBindViewHolder(MyViewHolder holder, final int position) {
		Glide.with(holder.mIvPhoto)
				.load(Uri.parse(mPhotoList.get(position).mUri))
				.into(holder.mIvPhoto);

		holder.mIvPhoto.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onClick(position);
				}
			}
		});

		holder.mIvDelete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mCallback != null) {
					mCallback.onDelete(position);
				}
			}
		});
	}

	@Override
	public int getItemCount() {
		return mPhotoList == null ? 0 : mPhotoList.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		public ImageView mIvPhoto;
		public ImageView mIvDelete;

		public MyViewHolder(View root) {
			super(root);
			mIvPhoto = root.findViewById(R.id.iv_photo);
			mIvDelete = root.findViewById(R.id.iv_delete);
		}
	}

	public interface Callback {
		void onClick(int position);

		void onDelete(int position);
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}
}
