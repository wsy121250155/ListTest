package com.example.test;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TestAdapter extends BaseAdapter {
	private LayoutInflater myInflater;
	private List<AbsItem> items;

	public TestAdapter(Context context, List<AbsItem> items) {
		myInflater = LayoutInflater.from(context);
		this.items = items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (null == convertView) {
			holder = new ViewHolder();
			convertView = myInflater.inflate(R.layout.list_item, null);
			holder.textView = (TextView) convertView
					.findViewById(R.id.textView);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.textView.setText(items.get(position).getContent() + position);
		return convertView;
	}

	class ViewHolder {
		TextView textView;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public int getCount() {
		return items.size();
	}
}
