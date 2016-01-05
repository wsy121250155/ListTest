package com.example.test;

import java.util.ArrayList;
import java.util.List;

import com.example.test.MyListView.ReachBottomListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.widget.BaseAdapter;

public class MainActivity extends Activity {
	// private String TAG = MainActivity.this.getClass().getName();
	private MyListView listView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initListView();
	}

	private List<AbsItem> items;
	private static int ONCE = 20;
	private BaseAdapter adapter;

	private void initListView() {
		listView = (MyListView) findViewById(R.id.listView);
		items = new ArrayList<AbsItem>();
		for (int i = 0; i < ONCE; i++) {
			items.add(new MyItem());
		}
		adapter = new TestAdapter(MainActivity.this, items);
		listView.setAdapter(adapter);
		listView.setReachBottomListener(new ReachBottomListener() {
			@Override
			public void reachBottom() {
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						for (int i = 0; i < ONCE; i++) {
							items.add(new MyItem());
						}
						adapter.notifyDataSetChanged();
						listView.loadComplete();
					}
				}, 2000);

			}
		});
	}
}
