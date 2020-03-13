package cn.hjf.csohelper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;

import cn.hjf.csohelper.data.model.Check;
import cn.hjf.csohelper.data.model.Photo;

public class ExportUtil {
	public static boolean export(Context context, Map<Check, List<Photo>> checkPhotoMap) {
		String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/cso助手/";
		for (Map.Entry<Check, List<Photo>> e : checkPhotoMap.entrySet()) {
			String dirPath = dir + e.getKey().mCso + "/" + e.getKey().mName + "/";
			for (Photo p : e.getValue()) {
				try {
					File dirFile = new File(dirPath);
					if (!dirFile.exists()) {
						if (!dirFile.mkdirs()) {
							return false;
						}
					}

					Bitmap bitmap = BitmapFactory.decodeStream(context.getContentResolver().openInputStream(Uri.parse(p.mUri)));

					File file = new File(dirFile, getFileName(p.mUri));
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

	private static String getFileName(String uri) {
		return uri.substring(uri.lastIndexOf("/") + 1);
	}
}
