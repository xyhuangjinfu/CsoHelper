package cn.hjf.csohelper.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.hjf.csohelper.data.model.Dir;
import cn.hjf.csohelper.data.model.Photo;

@Database(entities = {Dir.class, Photo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract DirDao dirDao();

	public abstract PhotoDao photoDao();
}
