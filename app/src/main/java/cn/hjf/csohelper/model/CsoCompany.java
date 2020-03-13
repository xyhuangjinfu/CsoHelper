package cn.hjf.csohelper.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "cso_company")
public class CsoCompany implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "name")
	public String nName;

	@Ignore
	public List<CheckItem> mCheckItemList = new ArrayList<>(0);
}
