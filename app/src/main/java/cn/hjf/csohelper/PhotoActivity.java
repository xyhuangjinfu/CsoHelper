package cn.hjf.csohelper;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.os.Environment.getExternalStorageDirectory;
import static android.os.Environment.getExternalStoragePublicDirectory;

public class PhotoActivity extends AppCompatActivity {

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_TAKE_PHOTO = 2;

//	ImageView imageView;

	private RecyclerView recyclerView;
	private PhotoListAdapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;
	private List<Uri> mUriList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo);

		Log.e("O_O",  getFilesDir().getAbsolutePath());
		Log.e("O_O",  getCacheDir().getAbsolutePath());
		Log.e("O_O",  Environment.getExternalStorageDirectory().getAbsolutePath());
		Log.e("O_O",  getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath());
		Log.e("O_O",  getExternalCacheDir().getAbsolutePath());
//		Log.e("O_O",  getExternalMediaDirs().getAbsolutePath());


//		imageView = findViewById(R.id.iv);
		recyclerView = findViewById(R.id.rv);

		// use a linear layout manager
		layoutManager = new GridLayoutManager(this, 3);
		recyclerView.setLayoutManager(layoutManager);

		// specify an adapter (see also next example)
		mAdapter = new PhotoListAdapter(mUriList);
		mAdapter.setCallback(new PhotoListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				viewImage(mUriList.get(position));
			}
		});
		recyclerView.setAdapter(mAdapter);
	}

	private void viewImage(Uri uri) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
//		Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//		intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//		intent.setType("image/*");
//		intent.setDataAndType(uri, "image/jpeg");
		intent.setDataAndType(uri, "image/*");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		if (intent.resolveActivity(getPackageManager()) != null) {
			startActivity(intent);
		}

//		if (intent.resolveActivity(getPackageManager()) != null) {
//			startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
//		}


//		File file = null;
//		final Intent intent = new Intent(Intent.ACTION_VIEW)//
//				.setDataAndType(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ?
//								FileProvider.getUriForFile(this,getPackageName() + ".provider", file) : Uri.fromFile(file),
//						"image/*").addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_photo, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_take_photo) {
//			takePhoto();
			mUri = dispatchTakePictureIntent();
		}
		return true;
	}

	Uri mUri = null;

	private void takePhoto() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
			Bundle extras = data.getExtras();
			Bitmap imageBitmap = (Bitmap) extras.get("data");
//			imageView.setImageBitmap(imageBitmap);
		} else 		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
//			Bundle extras = data.getExtras();
//			Bitmap imageBitmap = (Bitmap) extras.get("data");
//			imageView.setImageBitmap(imageBitmap);

			if (mUri != null) {
//				Glide.with(this)
//						.load(mUri)
//						.into(imageView);

				mUriList.add(mUri);
				mAdapter.notifyDataSetChanged();
			}
		}
	}

	private File createImageFile() throws IOException {
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = "JPEG_" + timeStamp + "_";
//		File storageDir = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
		File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
		File image = File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);

//		File image = new File(storageDir, imageFileName + ".jpg");

		// Save a file: path for use with ACTION_VIEW intents
//		currentPhotoPath = image.getAbsolutePath();
		return image;
	}


	private Uri dispatchTakePictureIntent() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				ex.printStackTrace();
				// Error occurred while creating the File
			}
			// Continue only if the File was successfully created
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
}
