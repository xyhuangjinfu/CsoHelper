package cn.hjf.csohelper.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import cn.hjf.csohelper.model.CsoCompany;

@Database(entities = {CsoCompany.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
	public abstract CsoCompanyDao csoCompanyDao();
}
