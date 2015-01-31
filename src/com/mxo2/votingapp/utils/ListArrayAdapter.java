package com.mxo2.votingapp.utils;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mxo2.votingapp.R;

/**
 * An ArrayAdapter to handle the layout inflation of all the ListViews used in
 * the application.
 **/
public class ListArrayAdapter extends ArrayAdapter<String> {

	private LayoutInflater li;
	private int resourceLayout;
	private ArrayList<String> column1;
	public ItemOptionClickListener mCallback;

	public interface ItemOptionClickListener {
		public void itemOptionClicked(int position);
	}

	public ListArrayAdapter(Context context, ArrayList<String> col_1,
			int resLayout) {
		super(context, 0, col_1);

		li = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		resourceLayout = resLayout;
		column1 = col_1;
	}

	public void setCallback(ItemOptionClickListener callback) {
		mCallback = callback;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = li.inflate(resourceLayout, parent, false);
			holder = new ViewHolder();
			if (resourceLayout == R.layout.rowlayout_items) {
				holder.txt_label1 = (TextView) convertView
						.findViewById(R.id.item_row);
			}
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (resourceLayout == R.layout.rowlayout_items) {
			holder.txt_label1.setText("");
			if (column1 != null) {
				if (column1.size() > 0) {
					if (!TextUtils.isEmpty(column1.get(position))) {
						if (!column1.get(position).equalsIgnoreCase("null")) {
							holder.txt_label1.setText(column1.get(position));
						}
					}
				}
			}
		}
		return convertView;

	}

	private static class ViewHolder {

		TextView txt_label1;

	}

}
