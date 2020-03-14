package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.OldPhoto;

@Dao
public interface PhotoDao {
	@Query("SELECT * FROM OldPhoto WHERE c_cso=:cso AND c_check=:check")
	List<OldPhoto> getAll(String cso, String check);

	@Insert
	void insert(OldPhoto photo);

	@Delete
	void delete(OldPhoto photo);
}
