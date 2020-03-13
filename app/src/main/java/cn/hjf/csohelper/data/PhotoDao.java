package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.Photo;

@Dao
public interface PhotoDao {
	@Query("SELECT * FROM t_photo WHERE c_cso=:cso AND c_check=:check")
	List<Photo> getAll(String cso, String check);

	@Insert
	void insert(Photo photo);

	@Delete
	void delete(Photo photo);
}
