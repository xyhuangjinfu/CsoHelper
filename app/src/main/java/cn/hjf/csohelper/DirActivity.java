package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.hjf.csohelper.data.AppDatabaseHolder;
import cn.hjf.csohelper.data.model.Dir;
import cn.hjf.csohelper.data.model.Photo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class DirActivity extends BaseActivity {

	private static final String KEY_FULL_DIR_LIST = "KEY_FULL_DIR_LIST";
	private static final int REQUEST_TAKE_PHOTO = 1234;

	private RecyclerView mRecyclerView;
	private GridLayoutManager mGridLayoutManager;
	private DirAdapter mDirAdapter;

	private List<Dir> mDirList = new ArrayList<>();
	private List<Photo> mPhotoList = new ArrayList<>();
	private Map<Dir, Integer[]> mDirInfoMap = new HashMap<>();

	private ArrayList<Dir> mFullDirList = new ArrayList<>();
	private Dir mCurrentDir;

	private Uri mTakePhotoUri = null;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dir);

		initLaunchParams();

		initTitle();

		initRecyclerView();

		fetchDirData();
	}

	@Override
	protected void onResume() {
		super.onResume();
		fetchDirData();
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		int position = mDirAdapter.getContextMenuPosition();
		if (mDirAdapter.getItemViewType(position) == DirAdapter.TYPE_DIR) {
			showDeleteDirDialog(mDirList.get(mDirAdapter.getDirIndex(position)));
		} else {
			showDeletePhotoDialog(mPhotoList.get(mDirAdapter.getPhotoIndex(position)));
		}
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		if (isRoot()) {
			inflater.inflate(R.menu.menu_dir_root, menu);
		} else if (isTopDir()) {
			inflater.inflate(R.menu.menu_dir_top, menu);
		} else if (isNormalDir()) {
			inflater.inflate(R.menu.menu_dir_normal, menu);
		}

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_take_photo) {
			mTakePhotoUri = takePhoto();
		} else if (item.getItemId() == R.id.menu_add_dir) {
			showCreateDirDialog();
		} else if (item.getItemId() == R.id.menu_export) {
			showExportDialog();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			if (mTakePhotoUri != null) {
				@Nullable String url = removeToGallery(mTakePhotoUri);
				if (url != null) {
					Photo photo = new Photo();
					photo.mUri = url;
					photo.mDirUuid = mCurrentDir.mUuid;

					savePhoto(photo);
				} else {
					Toast.makeText(this, "照片保存失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void initLaunchParams() {
		Intent intent = getIntent();

		ArrayList<Dir> parentDirList = (ArrayList<Dir>) intent.getSerializableExtra(KEY_FULL_DIR_LIST);
		if (parentDirList != null) {
			mFullDirList = parentDirList;

			mCurrentDir = mFullDirList.get(mFullDirList.size() - 1);
		}
	}

	private void initTitle() {
		if (mCurrentDir != null) {
			setTitle(mCurrentDir.mName);
		}
	}

	private void initRecyclerView() {
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
				final int halfSpace = 30;
				final int space = 60;

				if (adapterPosition < mDirList.size()) {
					outRect.left = space;
					outRect.top = halfSpace;
					outRect.right = space;
					outRect.bottom = halfSpace;

					if (adapterPosition == 0) {
						outRect.top = space;
					} else if (adapterPosition == mDirList.size() - 1) {
						outRect.bottom = 0;
					}
				} else {
					int newPosition = adapterPosition - mDirList.size();
					int column = newPosition % 2;
					int row = newPosition / 2;
					int allRowCount = (int) Math.ceil((parent.getAdapter().getItemCount() - mDirList.size()) * 1.0 / 2);

					outRect.top = halfSpace;
					outRect.bottom = halfSpace;
					outRect.left = halfSpace;
					outRect.right = halfSpace;

					if (column == 0) {
						outRect.left = space;
					} else if (column == 2 - 1) {
						outRect.right = space;
					}

					if (row == 0) {
						outRect.top = space;
					} else if (row == allRowCount - 1) {
						outRect.bottom = space;
					}
				}
			}
		});

		mDirAdapter = new DirAdapter(mDirList, mPhotoList, mDirInfoMap);
		mDirAdapter.setCallback(new DirAdapter.Callback() {
			@Override
			public void onDirClick(int dirIndex) {
				ArrayList<Dir> fullDirList = new ArrayList<>(mFullDirList);
				fullDirList.add(mDirList.get(dirIndex));

				startActivity(DirActivity.createIntent(DirActivity.this, fullDirList));
			}

			@Override
			public void onPhotoClick(int photoIndex) {
				viewPhoto(Uri.parse(mPhotoList.get(photoIndex).mUri));
			}
		});
		mRecyclerView.setAdapter(mDirAdapter);
	}

	private void showCreateDirDialog() {
		final View view = getLayoutInflater().inflate(R.layout.view_input, null);
		final EditText editText = view.findViewById(R.id.et);

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("输入分类名称")
				.setCancelable(false)
				.setView(view)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Dir dir = new Dir();
						dir.mName = editText.getText().toString();
						dir.mUuid = UUID.randomUUID().toString();
						if (!mFullDirList.isEmpty()) {
							Dir parent = mFullDirList.get(mFullDirList.size() - 1);
							dir.mParentUuid = parent.mUuid;
						}

						saveDir(dir);
					}
				})
				.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void showDeleteDirDialog(final Dir dir) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("删除分类")
				.setMessage("当前分类下的子分类和照片都会被删除，确定删除？")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deleteDir(dir);
					}
				})
				.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void showDeletePhotoDialog(final Photo photo) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("删除照片")
				.setMessage("确定删除这张照片？")
				.setCancelable(false)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						deletePhoto(photo);
					}
				})
				.setNegativeButton("取消", null);
		builder.create().show();
	}

	private void showExportDialog() {
		AlertDialog dialog = new AlertDialog.Builder(this).setTitle("导出图片")
				.setMessage(getExportMsg())
				.setCancelable(false)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						export();
					}
				})
				.setNegativeButton("取消", null)
				.create();
		dialog.show();
	}

	private CharSequence getExportMsg() {
		String s = "你的图片将会被导出到\n" + ExportUtil.getRootDir(this) + "\n目录下，确认导出？";
		SpannableString spannableString = new SpannableString(s);

		int start = s.indexOf("/");
		int end = s.lastIndexOf("/") + 1;

		spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		return spannableString;
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void fetchDirData() {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<Object[]>>() {
					@Override
					public ObservableSource<Object[]> apply(Object o) throws Exception {
						List<Dir> dirList = AppDatabaseHolder.getDb(DirActivity.this).dirDao().getDirList(mCurrentDir == null ? "" : mCurrentDir.mUuid);
						List<Photo> photoList = AppDatabaseHolder.getDb(DirActivity.this).photoDao().getPhotoList(mCurrentDir == null ? "" : mCurrentDir.mUuid);

						Map<Dir, Integer[]> map = new HashMap<>();
						for (Dir sd : dirList) {
							List<Dir> sdl = AppDatabaseHolder.getDb(DirActivity.this).dirDao().getDirList(sd.mUuid);
							List<Photo> pl = AppDatabaseHolder.getDb(DirActivity.this).photoDao().getPhotoList(sd.mUuid);

							map.put(sd, new Integer[]{sdl.size(), pl.size()});
						}

						Object[] objs = new Object[3];
						objs[0] = dirList;
						objs[1] = photoList;
						objs[2] = map;

						return Observable.just(objs);
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object[]>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object[] objs) {
						cancelLoadDialog();

						mDirList.clear();
						mDirList.addAll((List<Dir>) objs[0]);
						mPhotoList.clear();
						mPhotoList.addAll((List<Photo>) objs[1]);
						mDirInfoMap.clear();
						mDirInfoMap.putAll((Map<Dir, Integer[]>) objs[2]);
						mDirAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {

					}
				});
	}

	private void saveDir(final Dir dir) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(DirActivity.this).dirDao().insert(dir);
						return Observable.just("");
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchDirData();
					}
				});
	}

	private void deleteDir(final Dir dir) {
		showLoadDialog("删除中...");
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						DeleteUtil.deleteDir(DirActivity.this, dir);
						return Observable.just("");
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchDirData();
					}
				});
	}

	private void savePhoto(final Photo photo) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(DirActivity.this).photoDao().insert(photo);
						return Observable.just("");
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchDirData();
					}
				});
	}

	private void deletePhoto(final Photo photo) {
		showLoadDialog("删除中...");
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						DeleteUtil.deletePhoto(DirActivity.this, photo);
						return Observable.just("");
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchDirData();
					}
				});
	}

	private void export() {
		showLoadDialog("导出中...");
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<Boolean>>() {
					@Override
					public ObservableSource<Boolean> apply(Object o) throws Exception {
						boolean b = ExportUtil.exportDir(DirActivity.this, mFullDirList);
						return Observable.just(true);
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Boolean>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Boolean b) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, b ? "导出成功" : "导出失败", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(DirActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
					}
				});
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private boolean isRoot() {
		return mFullDirList.isEmpty();
	}

	private boolean isTopDir() {
		return mFullDirList.size() == 1;
	}

	private boolean isNormalDir() {
		return mFullDirList.size() > 1;
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private Uri takePhoto() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = createTempPhotoFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (photoFile != null) {
				Uri photoURI = FileProvider.getUriForFile(this,
						"cn.hjf.csohelper.fileprovider",
						photoFile);
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);

				return photoURI;
			}
		}

		return null;
	}

	private File createTempPhotoFile() throws IOException {
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);
		return image;
	}

	@Nullable
	private String removeToGallery(Uri uri) {
		try {
			InputStream is = getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			String url = MediaStore.Images.Media.insertImage(
					getContentResolver(),
					bitmap,
					getPhotoName(),
					"");

			getContentResolver().delete(uri, null, null);

			return url;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void viewPhoto(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "image/*");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}

	private String getPhotoName() {
		StringBuilder sb = new StringBuilder();

		for (Dir pa : mFullDirList) {
			sb.append(pa.mName);
			sb.append("_");
		}

		sb.append(mPhotoList.size() + 1);

		return sb.toString();
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	public static Intent createIntent(Context context, ArrayList<Dir> fullDirList) {
		Intent intent = new Intent(context, DirActivity.class);
		intent.putExtra(KEY_FULL_DIR_LIST, fullDirList);
		return intent;
	}
}
