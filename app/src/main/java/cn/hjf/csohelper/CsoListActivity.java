package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CsoListActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private CsoListAdapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;
	private List<String> mItemList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cso_detail);

		recyclerView = findViewById(R.id.rv);

		// use a linear layout manager
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		// specify an adapter (see also next example)
		mAdapter = new CsoListAdapter(mItemList);
		mAdapter.setCallback(new CsoListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				startActivity(CsoDetailActivity.createIntent(CsoListActivity.this, mItemList.get(position)));
			}
		});
		recyclerView.setAdapter(mAdapter);
//		recyclerView.createContextMenu();
//		recyclerView.setLongClickable(true);
//		registerForContextMenu(recyclerView);

	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		Toast.makeText(this, mItemList.get(mAdapter.getContextMenuPosition()), Toast.LENGTH_SHORT).show();

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_cso_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_add_cso) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			final EditText editText = (EditText) inflater.inflate(R.layout.view_input, null);
			builder.setTitle("输入CSO名称");
			builder.setView(editText)
					// Add action buttons
					.setPositiveButton("添加", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							mItemList.add(editText.getText().toString());
							mAdapter.notifyDataSetChanged();
						}
					})
					.setNegativeButton("取消", null);
			builder.create().show();
			showSoftKeyboard(editText);
		} else if (item.getItemId() == R.id.menu_export) {
			Toast.makeText(this, "导出", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager)
					getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}
}
