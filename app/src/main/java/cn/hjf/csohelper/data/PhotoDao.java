package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.Photo;

@Dao
public interface PhotoDao {
	@Query("SELECT * FROM t_photo WHERE c_dir_uuid=:dirUuid")
	List<Photo> getPhotoList(String dirUuid);

	@Insert
	void insert(Photo photo);

	@Delete
	void delete(Photo photo);
}
