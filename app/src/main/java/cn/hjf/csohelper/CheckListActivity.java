package cn.hjf.csohelper;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import cn.hjf.csohelper.data.model.OldPhoto;
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
	private Map<Check, List<OldPhoto>> mCheckPhotoMap = new HashMap<>();

	private String mCso;

	private AlertDialog mExportConfirmDialog;

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

		mExportConfirmDialog = new AlertDialog.Builder(this).setTitle("导出图片")
				.setMessage(getExportMsg())
				.setCancelable(false)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						export();
					}
				})
				.setNegativeButton("取消", null)
				.create();

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
			mExportConfirmDialog.show();
		}
		return true;
	}

	private CharSequence getExportMsg() {
		String s = "你的图片将会被导出到\n" + ExportUtil.getRootDir(this) + "\n目录下，确认导出？";
		SpannableString spannableString = new SpannableString(s);

		int start = s.indexOf("/");
		int end = s.lastIndexOf("/") + 1;

		spannableString.setSpan(new ForegroundColorSpan(Color.RED), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		spannableString.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, end, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

		return spannableString;
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
		final View view = getLayoutInflater().inflate(R.layout.view_input, null);
		final EditText editText = view.findViewById(R.id.et);

		AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setTitle("输入审核项目名称")
				.setView(view)
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
						Map<Check, List<OldPhoto>> checkPhotoMap = new HashMap<>();
						for (Check c : checkList) {
							List<OldPhoto> photos = AppDatabaseHolder.getDb(CheckListActivity.this).photoDao().getAll(mCso, c.mName);
							map.put(c.mName, photos.size());
							checkPhotoMap.put(c, photos);
						}

						Object[] objs = new Object[3];
						objs[0] = checkList;
						objs[1] = map;
						objs[2] = checkPhotoMap;
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

						mCheckPhotoMap.clear();
						mCheckPhotoMap.putAll((Map<Check, List<OldPhoto>>) objs[2]);

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

	private void export() {
		showLoadDialog("导出中...");
		Observable.just("")
				.flatMap(new Function<Object, ObservableSource<Boolean>>() {
					@Override
					public ObservableSource<Boolean> apply(Object o) throws Exception {
						boolean b = ExportUtil.export(CheckListActivity.this, mCheckPhotoMap);
						return Observable.just(b);
					}
				})
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(new Observer<Boolean>() {
					@Override
					public void onSubscribe(Disposable d) {

					}

					@Override
					public void onNext(Boolean b) {
						cancelLoadDialog();
						Toast.makeText(CheckListActivity.this, b ? "导出成功" : "导出失败", Toast.LENGTH_SHORT).show();
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
