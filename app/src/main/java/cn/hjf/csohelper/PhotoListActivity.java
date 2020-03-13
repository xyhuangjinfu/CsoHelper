package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
import java.util.List;

import cn.hjf.csohelper.data.AppDatabaseHolder;
import cn.hjf.csohelper.data.model.Photo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class PhotoListActivity extends BaseActivity {

	private static final int REQUEST_TAKE_PHOTO = 1234;

	private RecyclerView mRecyclerview;
	private PhotoListAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<Photo> mPhotoList = new ArrayList<>();
	private Uri mUri = null;

	private String mCso;
	private String mCheck;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_list);

		mCso = getIntent().getStringExtra("KEY_CSO");
		mCheck = getIntent().getStringExtra("KEY_CHECK");
		setTitle(mCheck);

		mRecyclerview = findViewById(R.id.rv);
		mLayoutManager = new GridLayoutManager(this, 3);
		mRecyclerview.setLayoutManager(mLayoutManager);
		mAdapter = new PhotoListAdapter(mPhotoList);
		mAdapter.setCallback(new PhotoListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				viewImage(Uri.parse(mPhotoList.get(position).mUri));
			}

			@Override
			public void onDelete(final int position) {
				showConfirmDeleteDialog(position);
			}
		});
		mRecyclerview.setAdapter(mAdapter);

		fetchPhotoList();
	}

	private void showConfirmDeleteDialog(final int position) {
		new AlertDialog.Builder(PhotoListActivity.this).setTitle("确定删除这张照片？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						deletePhoto(mPhotoList.get(position));
					}
				}).setNegativeButton("取消", null).create().show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_photo_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_take_photo) {
			mUri = takePicture();
		}
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
			if (mUri != null) {
				@Nullable String url = removeToGallery(mUri);
				if (url != null) {
					Photo photo = new Photo();
					photo.mCso = mCso;
					photo.mCheck = mCheck;
					photo.mUri = url;

					savePhoto(photo);
				} else {
					Toast.makeText(this, "照片保存失败", Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	public static Intent createIntent(Context context, String cso, String check) {
		Intent intent = new Intent(context, PhotoListActivity.class);
		intent.putExtra("KEY_CSO", cso);
		intent.putExtra("KEY_CHECK", check);
		return intent;
	}

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	private File createTempImageFile() throws IOException {
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

	private Uri takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			File photoFile = null;
			try {
				photoFile = createTempImageFile();
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

	@Nullable
	private String removeToGallery(Uri uri) {
		try {
			InputStream is = getContentResolver().openInputStream(uri);
			Bitmap bitmap = BitmapFactory.decodeStream(is);
			String url = MediaStore.Images.Media.insertImage(
					getContentResolver(),
					bitmap,
					getImageName(),
					"");

			getContentResolver().delete(mUri, null, null);

			return url;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

	private void viewImage(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(uri, "image/*");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}
	}

	private String getImageName() {
		return mCso + "_" + mCheck + "_" + (mPhotoList.size() + 1);
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void deletePhoto(final Photo photo) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(PhotoListActivity.this).photoDao().delete(photo);
						getContentResolver().delete(Uri.parse(photo.mUri), null, null);
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
						Toast.makeText(PhotoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchPhotoList();
					}
				});
	}

	private void savePhoto(final Photo photo) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(PhotoListActivity.this).photoDao().insert(photo);
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
						Toast.makeText(PhotoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchPhotoList();
					}
				});
	}

	private void fetchPhotoList() {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<List<Photo>>>() {
					@Override
					public ObservableSource<List<Photo>> apply(Object o) throws Exception {
						List<Photo> list = AppDatabaseHolder.getDb(PhotoListActivity.this).photoDao().getAll(mCso, mCheck);
						return Observable.just(list);
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<List<Photo>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(List<Photo> list) {
						cancelLoadDialog();
						mPhotoList.clear();
						mPhotoList.addAll(list);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(PhotoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {

					}
				});
	}
}
