package com.proinlab.functions;

import java.util.*;

import android.content.*;
import android.view.*;
import android.widget.*;

public class ListViewCustomAdapter extends BaseAdapter {
	Context maincon;
	LayoutInflater Inflater;
	ArrayList<String> arSrc;
	int layout;
	ArrayList<String> tagList;

	public ListViewCustomAdapter(Context context, int alayout,
			ArrayList<String> aarSrc, ArrayList<String> TagList) {
		maincon = context;
		Inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		arSrc = aarSrc;
		layout = alayout;
		tagList = TagList;
	}

	public int getCount() {
		return arSrc.size();
	}

	public String getItem(int position) {
		return arSrc.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = Inflater.inflate(layout, parent, false);
		}

		TextView Title = (TextView) convertView
				.findViewById(com.proinlab.impression.R.id.listview_category_contents_txt);
		Title.setText(arSrc.get(position));
		
		ImageView TagListView = (ImageView) convertView.findViewById(com.proinlab.impression.R.id.m01_03_ImageView_tag);
		TagListView.setBackgroundColor(Integer.parseInt(tagList.get(position)));
		
		convertView.setTag(position);

		return convertView;
	}

}