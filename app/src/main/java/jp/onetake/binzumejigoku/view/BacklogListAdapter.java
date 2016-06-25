package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import jp.onetake.binzumejigoku.R;

public class BacklogListAdapter extends ArrayAdapter<String> {
	private class ViewHolder {
		TextView logTextView;
	}

	public BacklogListAdapter(Context context, List<String> backlogList) {
		super(context, -1, backlogList);
	}

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
