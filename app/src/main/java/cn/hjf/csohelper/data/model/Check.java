package cn.hjf.csohelper.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

import java.io.Serializable;

@Entity(tableName = "t_check", primaryKeys = {"c_name", "c_cso"})
public class Check implements Serializable {

	@NonNull
	@ColumnInfo(name = "c_name")
	public String mName;

	@NonNull
	@ColumnInfo(name = "c_cso")
	public String mCso;
}
