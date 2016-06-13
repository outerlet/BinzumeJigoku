package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.SaveData;

public class SaveButton extends LinearLayout {
	private TextView mNameView;
	private TextView mDetailView;
	private TextView mTimeView;

	public SaveButton(Context context) {
		this(context, null);
	}

	public SaveButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(R.layout.view_save_button, this);
		mNameView = (TextView)view.findViewById(R.id.textview_name);
		mDetailView = (TextView)view.findViewById(R.id.textview_detail);
		mTimeView = (TextView)view.findViewById(R.id.textview_time);
	}

	public void setSaveData(SaveData saveData) {
		mNameView.setText(saveData.getName());

		if (saveData.hasSaved()) {
			int strId = getContext().getResources().getIdentifier(
					"title_section" + saveData.getSectionIndex(), "string", getContext().getPackageName());
			mDetailView.setText(getContext().getString(strId));

			mTimeView.setText(saveData.getTimeText());
		}
	}
}
