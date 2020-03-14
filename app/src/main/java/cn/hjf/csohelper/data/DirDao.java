package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.Dir;

@Dao
public interface DirDao {
	@Query("SELECT * FROM t_dir WHERE c_parent_uuid=:parentUuid")
	List<Dir> getDirList(String parentUuid);

	@Insert
	void insert(Dir dir);

	@Delete
	void delete(Dir dir);
}
