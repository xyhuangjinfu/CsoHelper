package cn.hjf.csohelper.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "photo_item")
public class PhotoItem implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "uri")
	public String mUri;

	@ColumnInfo(name = "cso")
	public String mCso;

	@ColumnInfo(name = "check")
	public String mCheck;
}
