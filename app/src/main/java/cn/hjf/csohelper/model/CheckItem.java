package cn.hjf.csohelper.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "check_item")
public class CheckItem  implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "name")
	public String mName;

	@ColumnInfo(name = "cso_name")
	public String mCsoName;

	@Ignore
	public List<PhotoItem> mUriList;
}
