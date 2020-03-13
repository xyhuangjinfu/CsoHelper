package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.Check;

@Dao
public interface CheckDao {
	@Query("SELECT * FROM t_check WHERE c_cso=:cso")
	List<Check> getAll(String cso);

	@Insert
	void insert(Check check);

	@Delete
	void delete(Check check);
}
