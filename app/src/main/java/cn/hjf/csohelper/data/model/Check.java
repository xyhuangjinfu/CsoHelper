package cn.hjf.csohelper.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_check")
public class Check implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "c_name")
	public String mName;

	@ColumnInfo(name = "c_cso")
	public String mCso;
}
