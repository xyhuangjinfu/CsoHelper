package cn.hjf.csohelper;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

	private static final String DEFAULT_LOAD_MSG = "加载中...";
	private AlertDialog mLoadDialog;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mLoadDialog = createAlertDialog();
	}

	private AlertDialog createAlertDialog() {
		return new AlertDialog.Builder(this).setMessage(DEFAULT_LOAD_MSG)
				.setCancelable(false).create();
	}

	protected void showLoadDialog(String msg) {
		mLoadDialog.setMessage(msg);
		mLoadDialog.show();
	}

	protected void showLoadDialog() {
		mLoadDialog.setMessage(DEFAULT_LOAD_MSG);
		mLoadDialog.show();
	}

	protected void cancelLoadDialog() {
		mLoadDialog.cancel();
	}
}
