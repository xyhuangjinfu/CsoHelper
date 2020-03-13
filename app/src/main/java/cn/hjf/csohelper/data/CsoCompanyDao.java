package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.model.CsoCompany;

@Dao
public interface CsoCompanyDao {
	@Query("SELECT * FROM csocompany")
	List<CsoCompany> getAll();

	@Insert
	void insert(CsoCompany csoCompany);

	@Delete
	void delete(CsoCompany csoCompany);
}
