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
	private LinearLayout mDetailView;
	private TextView mSectionView;
	private TextView mTimeView;
	private TextView mNoDataView;

	private SaveData mSaveData;

	public SaveButton(Context context) {
		this(context, null);
	}

	public SaveButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		View view = LayoutInflater.from(context).inflate(R.layout.view_save_button, this);
		mNameView = (TextView)view.findViewById(R.id.textview_name);
		mDetailView = (LinearLayout)view.findViewById(R.id.layout_detail);
		mSectionView = (TextView)view.findViewById(R.id.textview_section);
		mTimeView = (TextView)view.findViewById(R.id.textview_time);
		mNoDataView = (TextView)view.findViewById(R.id.textview_no_save_data);
	}

	public void setSaveData(SaveData saveData) {
		mSaveData = saveData;

		mNameView.setText(saveData.getName());

		if (saveData.hasSaved()) {
			mDetailView.setVisibility(View.VISIBLE);
			mNoDataView.setVisibility(View.INVISIBLE);

			int strId = getContext().getResources().getIdentifier(
					"title_section" + saveData.getSectionIndex(), "string", getContext().getPackageName());
			mSectionView.setText(getContext().getString(strId));

			mTimeView.setText(saveData.getTimeText());
		} else {
			mDetailView.setVisibility(View.INVISIBLE);
			mNoDataView.setVisibility(View.VISIBLE);
		}
	}

	public SaveData getSaveData() {
		return mSaveData;
	}
}
