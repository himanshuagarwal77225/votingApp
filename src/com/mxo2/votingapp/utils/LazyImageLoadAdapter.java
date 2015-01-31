package com.mxo2.votingapp.utils;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mxo2.votingapp.R;

/**
 * A BaseAdapter to handle the layout inflation of the image files and their
 * associated texts in a GridView/ListView.
 **/
public class LazyImageLoadAdapter extends BaseAdapter {

	private Activity mActivity;
	private ListItemSelectionListener mCallback;
	private ImageLoader imageLoader;
	private static LayoutInflater inflater = null;
	private ArrayList<String> column1;
	private ArrayList<String> column2;
	private ArrayList<String> url;
	private int resourceLayout;
	ViewHolder holder;
	Animation animFadein;

	public interface ListItemSelectionListener {
		public void onListItemSelected(int position);
	}

	public LazyImageLoadAdapter(Activity activity, ArrayList<String> col_1,
			ArrayList<String> col_2, ArrayList<String> url, int resLayout) {
		mActivity = activity;
		column1 = col_1;
		column2 = col_2;
		this.url = url;
		resourceLayout = resLayout;
		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Create ImageLoader object to download and show image in list
		// Call ImageLoader constructor to initialize FileCache
		imageLoader = new ImageLoader(activity.getApplicationContext());
		mCallback = (ListItemSelectionListener) activity;
	}

	public int getCount() {
		return column1.size();
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/********* Create a holder Class to contain inflated xml file elements *********/
	public static class ViewHolder {

		public TextView textview1;
		public TextView textview2;
		public ImageView imageview;
		public ImageView img_action;
		public TextView animateTextView;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		View vi = convertView;
		// ViewHolder holder;

		if (convertView == null) {

			/****** Inflate resourceLayout file for each row ( Defined below ) *******/
			vi = inflater.inflate(resourceLayout, parent, false);

			/****** View Holder Object to contain resourceLayout file elements ******/
			holder = new ViewHolder();

			if (resourceLayout == R.layout.rowlayout_parties) {
				holder.textview1 = (TextView) vi.findViewById(R.id.txt_abbr);
				holder.textview2 = (TextView) vi.findViewById(R.id.txt_name);
				holder.imageview = (ImageView) vi.findViewById(R.id.img_symbol);
				holder.img_action = (ImageView) vi
						.findViewById(R.id.img_action_vote);
			} else if (resourceLayout == R.layout.rowlayout_election || resourceLayout == R.layout.rowlayout_resultelection) {
				holder.textview1 = (TextView) vi.findViewById(R.id.txt_label1);
				holder.textview2 = (TextView) vi.findViewById(R.id.txt_label2);
				holder.imageview = (ImageView) vi.findViewById(R.id.img_logo);
				holder.animateTextView = (TextView) vi
						.findViewById(R.id.animateTextView);
			}

			/************ Set holder with LayoutInflater ************/
			vi.setTag(holder);
		} else
			holder = (ViewHolder) vi.getTag();

		holder.textview1.setText("");
		holder.textview2.setText("");

		if (column1 != null) {
			if (column1.size() > 0) {
				if (!TextUtils.isEmpty(column1.get(position))) {
					if (!column1.get(position).equalsIgnoreCase("null")) {
						holder.textview1.setText(column1.get(position));
					}
				}
			}
		}
		if (column2 != null) {
			if (column2.size() > 0) {
				if (!TextUtils.isEmpty(column2.get(position))) {
					if (!column2.get(position).equalsIgnoreCase("null")) {
						holder.textview2.setText(column2.get(position));
					}
				}
			}
		}

		ImageView image = holder.imageview;
		// DisplayImage function from ImageLoader Class
		imageLoader.DisplayImage(url.get(position), image);

		if (resourceLayout == R.layout.rowlayout_parties) {
			holder.img_action.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					// mCallback.onListItemSelected(position);
					return true;
				}
			});
		}

		if (resourceLayout == R.layout.rowlayout_election||resourceLayout == R.layout.rowlayout_resultelection) {
			// animation

			animFadein = AnimationUtils.loadAnimation(mActivity, R.anim.blink);
			animFadein.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationRepeat(Animation animation) {
					// TODO Auto-generated method stub

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					// TODO Auto-generated method stub

				}
			});
			holder.animateTextView.setVisibility(View.VISIBLE);
			holder.animateTextView.startAnimation(animFadein);
		}

		return vi;

	}

}