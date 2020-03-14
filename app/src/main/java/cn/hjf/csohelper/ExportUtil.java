package cn.hjf.csohelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import cn.hjf.csohelper.data.model.Check;
import cn.hjf.csohelper.data.model.Photo;

public class ExportUtil {

	public static String getRootDir(Context context) {
		String rootDir = context.getExternalFilesDir(null).getAbsolutePath() + "/图片导出/";
		return rootDir;
	}

	public static boolean export(Context context, Map<Check, List<Photo>> checkPhotoMap) {
		String rootDir = getRootDir(context);
		for (Map.Entry<Check, List<Photo>> e : checkPhotoMap.entrySet()) {
			String dirPath = rootDir + e.getKey().mCso + "/" + e.getKey().mName + "/";

			for (Photo p : e.getValue()) {
				try {
					File dirFile = new File(dirPath);
					if (!dirFile.exists()) {
						if (!dirFile.mkdirs()) {
							return false;
						}
					}

					Uri uri = Uri.parse(p.mUri);
					Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri));

					File file = new File(dirFile, getFileName(context, uri));
					FileOutputStream out = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
					out.flush();
					out.close();
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return true;
	}

	private static String getFileName(Context context, Uri uri) {
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		while (cursor.moveToNext()) {
			String displayName = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));
			cursor.close();
			return displayName;
		}
		return "未知命名";
	}
}
