package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import java.util.ArrayList;
import java.util.List;

import cn.hjf.csohelper.data.AppDatabase;
import cn.hjf.csohelper.model.CheckItem;
import cn.hjf.csohelper.model.CsoCompany;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CheckListActivity extends AppCompatActivity {

	private RecyclerView recyclerView;
	private CheckListAdapter mAdapter;
	private RecyclerView.LayoutManager layoutManager;
	private List<CheckItem> mItemList = new ArrayList<>();
	private AppDatabase mDatabase;

	private CsoCompany mCsoCompany;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cso_list);
		mCsoCompany = (CsoCompany) getIntent().getSerializableExtra("KEY_CSO");
		setTitle(mCsoCompany.nName);

		mDatabase = Room.databaseBuilder(getApplicationContext(),
				AppDatabase.class, "db_cso").build();



		Observable.just("")
				.flatMap(new Function<String, ObservableSource<List<CheckItem>>>() {
					@Override
					public ObservableSource<List<CheckItem>> apply(String s) throws Exception {
						return Observable.just(mDatabase.checkItemDao().getAll());
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<List<CheckItem>>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(List<CheckItem> checkItems) {
						mItemList.addAll(checkItems);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onComplete() {

					}
				});

		recyclerView = findViewById(R.id.rv);

		// use a linear layout manager
		layoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(layoutManager);

		// specify an adapter (see also next example)
		mAdapter = new CheckListAdapter(mItemList);
		mAdapter.setCallback(new CheckListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				startActivity(PhotoListActivity.createIntent(CheckListActivity.this, mItemList.get(position).mName));
			}
		});
		recyclerView.setAdapter(mAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_cso_detail, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_add_item) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = getLayoutInflater();

			// Inflate and set the layout for the dialog
			// Pass null as the parent view because its going in the dialog layout
			final EditText editText = (EditText) inflater.inflate(R.layout.view_input, null);
			builder.setTitle("输入项目名称");
			builder.setView(editText)
					// Add action buttons
					.setPositiveButton("添加", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int id) {
							CheckItem checkItem = new CheckItem();
							checkItem.mCsoName = mCsoCompany.nName;
							checkItem.mName = editText.getText().toString();

							save(checkItem);
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

	private void save(final CheckItem checkItem) {
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						mDatabase.checkItemDao().insert(checkItem);
						return Observable.just("");
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object o) {

					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(CheckListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						mItemList.add(checkItem);
						mAdapter.notifyDataSetChanged();
					}
				});
	}

	public void showSoftKeyboard(View view) {
		if (view.requestFocus()) {
			InputMethodManager imm = (InputMethodManager)
					getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
		}
	}

	public static Intent createIntent(Context context, CsoCompany csoCompany) {
		Intent intent = new Intent(context, CheckListActivity.class);
		intent.putExtra("KEY_CSO", csoCompany);
		return intent;
	}
}
