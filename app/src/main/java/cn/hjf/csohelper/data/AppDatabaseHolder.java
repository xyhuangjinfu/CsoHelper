package cn.hjf.csohelper.data;

import android.content.Context;

import androidx.room.Room;

public final class AppDatabaseHolder {

	private static AppDatabase sDb = null;

	public static synchronized AppDatabase getDb(Context context) {
		if (sDb == null) {
			sDb = Room.databaseBuilder(
					context.getApplicationContext(),
					AppDatabase.class,
					"db_cso")
					.build();
		}

		return sDb;
	}
}
