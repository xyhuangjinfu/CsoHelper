package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.data.model.Cso;

@Dao
public interface CsoDao {
	@Query("SELECT * FROM t_cso")
	List<Cso> getAll();

	@Insert
	void insert(Cso csoCompany);

	@Delete
	void delete(Cso csoCompany);
}
