package cn.hjf.csohelper.data.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "t_cso")
public class Cso implements Serializable {

	@PrimaryKey
	@NonNull
	@ColumnInfo(name = "c_name")
	public String nName;
}
