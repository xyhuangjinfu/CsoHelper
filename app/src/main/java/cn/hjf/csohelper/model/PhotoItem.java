package cn.hjf.csohelper.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "check_item")
public class PhotoItem  implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "name")
	public String mUri;

	@ColumnInfo(name = "cso_name")
	public String mCsoName;

	@ColumnInfo(name = "check_item_name")
	public String mCheckItemName;
}
