package cn.hjf.csohelper;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	private AlertDialog mLoadDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoadDialog = createAlertDialog();
	}

	private AlertDialog createAlertDialog() {
		return new AlertDialog.Builder(this).setMessage("加载中")
				.setCancelable(false).create();
	}

	protected void showLoadDialog() {
		mLoadDialog.show();
	}

	protected void cancelLoadDialog() {
		mLoadDialog.cancel();
	}
}
