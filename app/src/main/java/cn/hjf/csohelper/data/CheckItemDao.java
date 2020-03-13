package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.model.CheckItem;

@Dao
public interface CheckItemDao {
	@Query("SELECT * FROM check_item")
	List<CheckItem> getAll();

	@Insert
	void insert(CheckItem checkItem);

	@Delete
	void delete(CheckItem checkItem);

	@Query("SELECT * FROM check_item WHERE cso_name=:csoName")
	List<CheckItem> findByCso(String csoName);
}
