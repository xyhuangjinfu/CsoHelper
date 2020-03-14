package cn.hjf.csohelper;

import android.app.Activity;
import android.net.Uri;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.hjf.csohelper.data.model.Dir;
import cn.hjf.csohelper.data.model.Photo;

public class DirAdapter extends RecyclerView.Adapter {

	public static final int TYPE_DIR = 1;
	public static final int TYPE_PHOTO = 2;

	private List<Dir> mDirList;
	private List<Photo> mPhotoList;

	private Callback mCallback;

	private int mContextMenuPosition;

	public DirAdapter(@NonNull List<Dir> dirList, @NonNull List<Photo> photoList) {
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
	public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
		if (holder instanceof DirVH) {
			DirVH dirVH = (DirVH) holder;
			dirVH.mTvDir.setText(mDirList.get(position).mName);

			dirVH.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mCallback != null) {
						mCallback.onDirClick(position);
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
			return;
		}

		if (holder instanceof PhotoVH) {
			PhotoVH photoVH = (PhotoVH) holder;
			Glide.with(photoVH.mTvPhoto)
					.load(Uri.parse(mPhotoList.get(getPhotoIndex(position)).mUri))
					.into(photoVH.mTvPhoto);

			photoVH.itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mCallback != null) {
						mCallback.onPhotoClick(getPhotoIndex(position));
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

			return;
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

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	public interface Callback {
		void onDirClick(int dirIndex);

		void onPhotoClick(int photoIndex);
	}

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public int getContextMenuPosition() {
		return mContextMenuPosition;
	}

	public int getDirIndex(int position) {
		return position;
	}

	public int getPhotoIndex(int position) {
		return position - mDirList.size();
	}
}
