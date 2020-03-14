package cn.hjf.csohelper;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class DirAdapter extends RecyclerView.Adapter {

	private static final int TYPE_DIR = 1;
	private static final int TYPE_PHOTO = 2;

	private List<String> mDirList;
	private List<String> mPhotoList;

	public DirAdapter(@NonNull List<String> dirList, @NonNull List<String> photoList) {
		mDirList = dirList;
		mPhotoList = photoList;
	}

	@NonNull
	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		if (viewType == TYPE_DIR) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_dir_item, null);
			return new DirVH(view);
		}
		if (viewType == TYPE_PHOTO) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_photo, null);
			return new PhotoVH(view);
		}
		return null;
	}

	@Override
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
		if (holder instanceof DirVH) {
			DirVH dirVH = (DirVH) holder;
			dirVH.mTvDir.setText(mDirList.get(position));
			return;
		}

		if (holder instanceof PhotoVH) {
			PhotoVH photoVH = (PhotoVH) holder;
			Glide.with(photoVH.mTvPhoto)
					.load(mPhotoList.get(getPhotoIndex(position)))
					.into(photoVH.mTvPhoto);
		}
	}

	@Override
	public int getItemViewType(int position) {
		if (position < mDirList.size()) {
			return TYPE_DIR;
		} else {
			return TYPE_PHOTO;
		}
	}

	@Override
	public int getItemCount() {
		return mDirList.size() + mPhotoList.size();
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private int getPhotoIndex(int position) {
		return position - mDirList.size();
	}

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	public static class DirVH extends RecyclerView.ViewHolder {

		private TextView mTvDir;

		public DirVH(@NonNull View itemView) {
			super(itemView);
			mTvDir = itemView.findViewById(R.id.tv_item);
		}
	}

	public static class PhotoVH extends RecyclerView.ViewHolder {

		private ImageView mTvPhoto;

		public PhotoVH(@NonNull View itemView) {
			super(itemView);
			mTvPhoto = itemView.findViewById(R.id.iv_photo);
		}
	}
}
