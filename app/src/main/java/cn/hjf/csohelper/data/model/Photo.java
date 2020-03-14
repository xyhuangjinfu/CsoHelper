package cn.hjf.csohelper.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_photo")
public class Photo implements Serializable {
	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "c_uri")
	public String mUri;

	@NonNull
	@ColumnInfo(name = "c_dir_uuid")
	public String mDirUuid;
}
