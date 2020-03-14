package cn.hjf.csohelper;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class DirActivity extends BaseActivity {

	private RecyclerView mRecyclerView;
	private GridLayoutManager mGridLayoutManager;
	private DirAdapter mDirAdapter;
	private List<String> mDirList = new ArrayList<>();
	private List<String> mPhotoList = new ArrayList<>();

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dir);

		mDirList.add("dir 1");
		mDirList.add("dir 2");
		mDirList.add("dir 3");
		mDirList.add("dir 4");

		mPhotoList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584182611064&di=4dfd022b632fe5c0fb8fe443bfd73025&imgtype=0&src=http%3A%2F%2Ft7.baidu.com%2Fit%2Fu%3D378254553%2C3884800361%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D1280%26h%3D2030");
		mPhotoList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584182644745&di=2c7c63824b7a8224530b8b0902196ea7&imgtype=0&src=http%3A%2F%2Ft7.baidu.com%2Fit%2Fu%3D3616242789%2C1098670747%26fm%3D79%26app%3D86%26f%3DJPEG%3Fw%3D900%26h%3D1350");
		mPhotoList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584182677980&di=2c0cb7f85fbb2ecf5426255f62971ba8&imgtype=0&src=http%3A%2F%2Fa3.att.hudong.com%2F50%2F05%2F01300000763638130719050981141.jpg");
		mPhotoList.add("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1584182879626&di=6ef7368203b3742f9f9e14f46d83552b&imgtype=0&src=http%3A%2F%2Fa2.att.hudong.com%2F50%2F03%2F01300000167059121860035875425.jpg");

		mRecyclerView = findViewById(R.id.rv);

		mGridLayoutManager = new GridLayoutManager(this, 2);
		mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				if (position < mDirList.size()) {
					return 2;
				}
				return 1;
			}
		});
		mRecyclerView.setLayoutManager(mGridLayoutManager);

		mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
			@Override
			public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
				int adapterPosition = parent.getChildAdapterPosition(view);

				if (adapterPosition < mDirList.size()) {
					outRect.left = 60;
					outRect.top = 30;
					outRect.right = 60;
					outRect.bottom = 30;

					if (adapterPosition == 0) {
						outRect.top = 30 * 2;
					} else if (adapterPosition == mDirList.size() - 1) {
						outRect.bottom = 0;
					}
				} else {
					int newPosition = adapterPosition - mDirList.size();
					int column = newPosition % 2;
					int row = newPosition / 2;
					int allRowCount = (int) Math.ceil((parent.getAdapter().getItemCount() - mDirList.size()) * 1.0 / 2);

					outRect.top = 30;
					outRect.bottom = 30;
					outRect.left = 30;
					outRect.right = 30;

					if (column == 0) {
						outRect.left = 30 * 2;
					} else if (column == 2 - 1) {
						outRect.right = 30 * 2;
					}

					if (row == 0) {
						outRect.top = 30 * 2;
					} else if (row == allRowCount - 1) {
						outRect.bottom = 30 * 2;
					}
				}
			}
		});


		mDirAdapter = new DirAdapter(mDirList, mPhotoList);
		mDirAdapter.setCallback(new DirAdapter.Callback() {
			@Override
			public void onDirClick(int dirIndex) {
				Toast.makeText(DirActivity.this, mDirList.get(dirIndex), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPhotoClick(int photoIndex) {
				Toast.makeText(DirActivity.this, mPhotoList.get(photoIndex), Toast.LENGTH_SHORT).show();
			}
		});
		mRecyclerView.setAdapter(mDirAdapter);
	}
}
