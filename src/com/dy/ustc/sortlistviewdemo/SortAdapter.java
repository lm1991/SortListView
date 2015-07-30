package com.dy.ustc.sortlistviewdemo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer {

	// �����ֻ�ܱ��һ�������ֶ�

	private List<SortModel> list = null;
	private CharacterParser characterParser;

	private Context mContext;

	public SortAdapter(Context mContext, List<SortModel> list) {
		this.mContext = mContext;
		this.list = list;
		this.characterParser = CharacterParser.getInstance();
	}

	private String filterStr = null;

	public void updateListView(List<SortModel> list, String filterStr) {
		this.list = list;
		this.filterStr = filterStr;
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return this.list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item, null);
			viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.title);
			viewHolder.tvLetter = (TextView) convertView.findViewById(R.id.catalog);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		// ����position��ȡ���������ĸ��Char asciiֵ
		int section = getSectionForPosition(position);
		// �����ǰλ�õ��ڸ÷�������ĸ��Char��λ�� ������Ϊ�ǵ�һ�γ���
		if (position == getPositionForSection(section)) {
			viewHolder.tvLetter.setVisibility(View.VISIBLE);
			viewHolder.tvLetter.setText(mContent.getSortLetters());
		} else {
			viewHolder.tvLetter.setVisibility(View.GONE);
		}
		String name = this.list.get(position).getName();
		if (!TextUtils.isEmpty(filterStr)) {
			SpannableString ss = getSpannableString(name);
			viewHolder.tvTitle.setText(ss);
		} else
			viewHolder.tvTitle.setText(this.list.get(position).getName());
		return convertView;
	}
	
	private SpannableString getSpannableString(String name) {
		SpannableString ss = new SpannableString(name);
		int index = name.toLowerCase(Locale.US).indexOf(filterStr.toLowerCase(Locale.US));
		String tmp = name.toLowerCase(Locale.US);
		String replace = "";
		for (int i = 0; i < filterStr.length(); i++)
			replace += "-";
		if (index != -1) {
			while ((index != -1)) {
				ss.setSpan(new ForegroundColorSpan(Color.RED), index, index + filterStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
				tmp = tmp.replaceFirst(filterStr.toLowerCase(Locale.US), replace);
				index = tmp.indexOf(filterStr.toLowerCase(Locale.US));
			}
		} else {
			boolean contain = true;
			String src = "";
			while (contain) {
				int start = 0, end = name.length();
				while (contain) {
					end--;
					contain = characterParser.getSelling(tmp.substring(start, end)).contains(filterStr.toLowerCase(Locale.US));
				}
				end++;
				contain = true;
				while (contain) {
					start++;
					contain = characterParser.getSelling(tmp.substring(start, end)).contains(filterStr.toLowerCase(Locale.US));
				}
				start--;
				ss.setSpan(new ForegroundColorSpan(Color.RED), start, end,
				// setSpanʱ��Ҫָ����
				// flag,Spanned.SPAN_EXCLUSIVE_EXCLUSIVE(ǰ�󶼲�����).
						Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
				src = tmp.substring(start, end);
				if (replace.contains("-")) {
					replace = "";
					for (int i = start; i < end; i++)
						replace += "+";
				}
				tmp = tmp.replaceFirst(src, replace);
				contain = characterParser.getSelling(tmp).contains(filterStr.toLowerCase(Locale.US));
			}
		}
		return ss;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	/**
	 * ���ݷ��������ĸ��Char asciiֵ��ȡ���һ�γ��ָ�����ĸ��λ��
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		for (int i = 0; i < getCount(); i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase().charAt(0);
			if (firstChar == sectionIndex) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * ����ListView�ĵ�ǰλ�û�ȡ���������ĸ��Char asciiֵ
	 */
	@Override
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
	}

	/**
	 * ��ȡӢ�ĵ�����ĸ����Ӣ����ĸ��#���档
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// ������ʽ���ж�����ĸ�Ƿ���Ӣ����ĸ
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

}
