package cn.hjf.csohelper;

import android.content.Context;
import android.net.Uri;

import java.util.List;

import cn.hjf.csohelper.data.AppDatabaseHolder;
import cn.hjf.csohelper.data.model.Dir;
import cn.hjf.csohelper.data.model.Photo;

public class DeleteUtil {

	public static void deleteDir(Context context, Dir dir) {
		List<Dir> subDirList = AppDatabaseHolder.getDb(context).dirDao().getDirList(dir.mUuid);
		List<Photo> photoList = AppDatabaseHolder.getDb(context).photoDao().getPhotoList(dir.mUuid);

		for (Photo photo : photoList) {
			deletePhoto(context, photo);
		}

		AppDatabaseHolder.getDb(context).dirDao().delete(dir);

		for (Dir d : subDirList) {
			deleteDir(context, d);
		}
	}

	public static void deletePhoto(Context context, Photo photo) {
		context.getContentResolver().delete(Uri.parse(photo.mUri), null, null);

		AppDatabaseHolder.getDb(context).photoDao().delete(photo);
	}
}
