package cn.hjf.csohelper.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.hjf.csohelper.model.CheckItem;
import cn.hjf.csohelper.model.CsoCompany;
import cn.hjf.csohelper.model.PhotoItem;

@Database(entities = {CsoCompany.class, CheckItem.class, PhotoItem.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract CsoCompanyDao csoCompanyDao();
	public abstract CheckItemDao checkItemDao();
	public abstract CsoDao csoDao();
}
