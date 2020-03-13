package cn.hjf.csohelper.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class CsoCompany {

	@PrimaryKey
	@NonNull
	public String name;
}
