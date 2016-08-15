package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.onetake.binzumejigoku.R;

/**
 * バックログを表示するためのアダプタクラス
 */
public class BacklogListAdapter extends ArrayAdapter<String> {
	// ViewHolder
	private class ViewHolder {
		TextView logTextView;
	}

	/**
	 * コンストラクタ
	 * @param context		コンテキスト
	 * @param backlogList	バックログとして表示する文字列が格納されたリスト
	 */
	public BacklogListAdapter(Context context, List<String> backlogList) {
		super(context, -1, backlogList);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(R.layout.view_backlog_singleline, parent, false);

			holder = new ViewHolder();
			holder.logTextView = (TextView)convertView.findViewById(R.id.textview_backlog_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder)convertView.getTag();
		}

		holder.logTextView.setText(getItem(position));

		return convertView;
	}
}
