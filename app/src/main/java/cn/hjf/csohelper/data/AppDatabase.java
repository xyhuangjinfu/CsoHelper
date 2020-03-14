package cn.hjf.csohelper.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.hjf.csohelper.data.model.Check;
import cn.hjf.csohelper.data.model.Cso;
import cn.hjf.csohelper.data.model.Dir;
import cn.hjf.csohelper.data.model.OldPhoto;
import cn.hjf.csohelper.data.model.Photo;

@Database(entities = {Cso.class, Check.class, OldPhoto.class, Dir.class, Photo.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract CsoDao csoDao();

	public abstract CheckDao checkDao();

	public abstract OldPhotoDao oldPhotoDao();

	public abstract DirDao dirDao();

	public abstract PhotoDao photoDao();
}
