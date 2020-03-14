package cn.hjf.csohelper;

import android.os.Bundle;

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

		mDirAdapter = new DirAdapter(mDirList, mPhotoList);
		mRecyclerView.setAdapter(mDirAdapter);

	}
}
