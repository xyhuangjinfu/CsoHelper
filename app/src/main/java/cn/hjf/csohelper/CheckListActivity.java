package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hjf.csohelper.data.AppDatabaseHolder;
import cn.hjf.csohelper.data.model.Check;
import cn.hjf.csohelper.data.model.Photo;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CheckListActivity extends BaseActivity {

	private RecyclerView mRecyclerView;
	private CheckListAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<Check> mCheckList = new ArrayList<>();
	private Map<String, Integer> mPhotoCountMap = new HashMap<>();

	private String mCso;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_check_list);

		mCso = getIntent().getStringExtra("KEY_CSO");
		setTitle(mCso);

		mRecyclerView = findViewById(R.id.rv);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		mAdapter = new CheckListAdapter(mCheckList, mPhotoCountMap);
		mAdapter.setCallback(new CheckListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				startActivity(PhotoListActivity.createIntent(CheckListActivity.this, mCso, mCheckList.get(position).mName));
			}
		});
		mRecyclerView.setAdapter(mAdapter);

		fetchCheckList();
	}

	@Override
	protected void onResume() {
		super.onResume();

		fetchCheckList();
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		deleteCheck(mCheckList.get(mAdapter.getContextMenuPosition()));
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_check_list, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		if (item.getItemId() == R.id.menu_add_item) {
			showCreateDialog();
		} else if (item.getItemId() == R.id.menu_export) {
			Toast.makeText(this, "导出", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	public static Intent createIntent(Context context, String cso) {
		Intent intent = new Intent(context, CheckListActivity.class);
		intent.putExtra("KEY_CSO", cso);
		return intent;
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void showCreateDialog() {
		final EditText editText = (EditText) getLayoutInflater().inflate(R.layout.view_input, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("输入审核项目名称")
				.setView(editText)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Check checkItem = new Check();
						checkItem.mCso = mCso;
						checkItem.mName = editText.getText().toString();

						saveCheck(checkItem);
					}
				})
				.setNegativeButton("取消", null);
		builder.create().show();
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void saveCheck(final Check check) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(CheckListActivity.this).checkDao().insert(check);
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
						cancelLoadDialog();
						Toast.makeText(CheckListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchCheckList();
					}
				});
	}

	private void deleteCheck(final Check check) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(CheckListActivity.this).checkDao().delete(check);
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
						cancelLoadDialog();
						Toast.makeText(CheckListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchCheckList();
					}
				});
	}

	private void fetchCheckList() {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<String, ObservableSource<Object[]>>() {
					@Override
					public ObservableSource<Object[]> apply(String s) throws Exception {
						List<Check> checkList = AppDatabaseHolder.getDb(CheckListActivity.this).checkDao().getAll(mCso);

						Map<String, Integer> map = new HashMap<>();
						for (Check c : checkList) {
							List<Photo> photos = AppDatabaseHolder.getDb(CheckListActivity.this).photoDao().getAll(mCso, c.mName);
							map.put(c.mName, photos.size());
						}

						Object[] objs = new Object[2];
						objs[0] = checkList;
						objs[1] = map;
						return Observable.just(objs);
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Object[]>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Object[] objs) {
						cancelLoadDialog();
						mCheckList.clear();
						mCheckList.addAll((List<Check>) objs[0]);

						mPhotoCountMap.clear();
						mPhotoCountMap.putAll((Map<String, Integer>) objs[1]);

						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(CheckListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {

					}
				});
	}
}
