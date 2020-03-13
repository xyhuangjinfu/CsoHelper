package cn.hjf.csohelper;

import android.content.DialogInterface;
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
import cn.hjf.csohelper.data.model.Cso;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CsoListActivity extends BaseActivity {

	private RecyclerView mRecyclerView;
	private CsoListAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<Cso> mCsoList = new ArrayList<>();
	private Map<String, Integer> mCsoCountMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cso_list);

		mRecyclerView = findViewById(R.id.rv);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		mAdapter = new CsoListAdapter(mCsoList, mCsoCountMap);
		mAdapter.setCallback(new CsoListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				startActivity(CheckListActivity.createIntent(CsoListActivity.this, mCsoList.get(position).nName));
			}
		});
		mRecyclerView.setAdapter(mAdapter);
	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		deleteCso(mCsoList.get(mAdapter.getContextMenuPosition()));
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
			showCreateDialog();
		} else if (item.getItemId() == R.id.menu_export) {
			Toast.makeText(this, "导出", Toast.LENGTH_SHORT).show();
		}
		return true;
	}

	/**
	 * ***************************************************************************************************************
	 * <p>
	 * ***************************************************************************************************************
	 */

	private void showCreateDialog() {
		final EditText editText = (EditText) getLayoutInflater().inflate(R.layout.view_input, null);

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("输入CSO名称")
				.setView(editText)
				.setPositiveButton("添加", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						Cso company = new Cso();
						company.nName = editText.getText().toString();

						saveCso(company);
					}
				})
				.setNegativeButton("取消", null);
		builder.create().show();
	}

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	private void deleteCso(final Cso cso) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(CsoListActivity.this).csoDao().delete(cso);
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
						Toast.makeText(CsoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchCsoList();
					}
				});
	}

	private void saveCso(final Cso cso) {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						AppDatabaseHolder.getDb(CsoListActivity.this).csoDao().insert(cso);
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
						Toast.makeText(CsoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						cancelLoadDialog();
						fetchCsoList();
					}
				});
	}

	private void fetchCsoList() {
		showLoadDialog();
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<Object[]>>() {
					@Override
					public ObservableSource<Object[]> apply(Object o) throws Exception {
						List<Cso> csoList = AppDatabaseHolder.getDb(CsoListActivity.this).csoDao().getAll();

						Map<String, Integer> map = new HashMap<>();
						for (Cso cso : csoList) {
							List<Check> checks = AppDatabaseHolder.getDb(CsoListActivity.this).checkDao().getAll(cso.nName);
							map.put(cso.nName, checks.size());
						}

						Object[] objs = new Object[2];
						objs[0] = csoList;
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
						mCsoList.addAll((List<Cso>) objs[0]);
						mCsoCountMap.putAll((Map<String, Integer>) objs[1]);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						cancelLoadDialog();
						Toast.makeText(CsoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {

					}
				});
	}
}
