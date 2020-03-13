package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hjf.csohelper.data.AppDatabaseHolder;
import cn.hjf.csohelper.model.CsoCompany;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class CsoListActivity extends AppCompatActivity {

	private RecyclerView mRecyclerView;
	private CsoListAdapter mAdapter;
	private RecyclerView.LayoutManager mLayoutManager;
	private List<String> mCsoList = new ArrayList<>();
	private Map<String, String> mCsoCountMap = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cso_list);

		mRecyclerView = findViewById(R.id.rv);
		mLayoutManager = new LinearLayoutManager(this);
		mRecyclerView.setLayoutManager(mLayoutManager);

		// specify an adapter (see also next example)

		mAdapter = new CsoListAdapter(mCsoList, mCsoCountMap);
		mAdapter.setCallback(new CsoListAdapter.Callback() {
			@Override
			public void onClick(int position) {
				startActivity(CheckListActivity.createIntent(CsoListActivity.this, mItemList.get(position)));
			}
		});
		mRecyclerView.setAdapter(mAdapter);

	}

	@Override
	public boolean onContextItemSelected(@NonNull MenuItem item) {
		Toast.makeText(this, mItemList.get(mAdapter.getContextMenuPosition()).nName, Toast.LENGTH_SHORT).show();

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
							CsoCompany company = new CsoCompany();
							company.nName = editText.getText().toString();

							saveCso(company);
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

	private void saveCso(final CsoCompany company) {
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<String>>() {
					@Override
					public ObservableSource<String> apply(Object o) throws Exception {
						mDatabase.csoCompanyDao().insert(company);
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
						Toast.makeText(CsoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {
						mItemList.add(company);
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

	/**
	 * ***************************************************************************************************************
	 * //
	 * ***************************************************************************************************************
	 */

	private void fetchCsoList() {
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<Object[]>>() {
					@Override
					public ObservableSource<Object[]> apply(Object o) throws Exception {
						List<String> csoList = AppDatabaseHolder.getDb(CsoListActivity.this).csoDao().getCsoList();

						Map<String, String> map = new HashMap<>();
						for (String cso : csoList) {
							String count = AppDatabaseHolder.getDb(CsoListActivity.this).csoDao().getCheckListCount(cso);
							map.put(cso, count);
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
						mCsoList.addAll((List<String>) objs[0]);
						mCsoCountMap.putAll((Map<String, String>) objs[1]);
						mAdapter.notifyDataSetChanged();
					}

					@Override
					public void onError(Throwable e) {
						Toast.makeText(CsoListActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
					}

					@Override
					public void onComplete() {

					}
				});
	}
}
