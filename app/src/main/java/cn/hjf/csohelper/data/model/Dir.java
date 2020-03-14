package cn.hjf.csohelper.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_dir")
public class Dir implements Serializable {
	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "c_uuid")
	public String mUuid;

	@NonNull
	@ColumnInfo(name = "c_name")
	public String mName;

	@ColumnInfo(name = "c_parent_uuid")
	public String mParentUuid;
}
