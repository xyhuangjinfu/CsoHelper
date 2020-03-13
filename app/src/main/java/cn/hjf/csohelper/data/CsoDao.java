package cn.hjf.csohelper.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import cn.hjf.csohelper.model.PhotoItem;

@Dao
public interface CsoDao {
	@Query("SELECT DISTINCT cso FROM photo_item")
	List<String> getCsoList();

	@Query("SELECT `check` FROM photo_item WHERE cso=:cso")
	List<String> getCheckList(String cso);

	@Query("SELECT `uri` FROM photo_item WHERE cso=:cso AND `check`=:check")
	List<String> getPhotoList(String cso, String check);

	@Query("SELECT COUNT(*) FROM photo_item WHERE cso=:cso")
	String getCheckListCount(String cso);

	@Query("SELECT COUNT(*) FROM photo_item WHERE cso=:cso AND `check`=:check")
	String getPhotokListCount(String cso, String check);

	@Insert
	void insert(PhotoItem photoItem);

	@Query("DELETE FROM photo_item WHERE cso=:cso")
	void deleteCso(String cso);

	@Query("DELETE FROM photo_item WHERE cso=:cso AND `check`=:check")
	void deleteCheck(String cso, String check);

	@Query("DELETE FROM photo_item WHERE uri=:uri")
	void deletePhoto(String uri);
}
