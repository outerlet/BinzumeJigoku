package jp.onetake.binzumejigoku.fragment.dialog;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;

public class SectionFragment extends Fragment {
	private final static String KEY_SECTION_INDEX = "SectionFragment.KEY_SECTION_INDEX";

	public static SectionFragment newInstance(int sectionIndex) {
		Bundle params = new Bundle();
		params.putInt(KEY_SECTION_INDEX, sectionIndex);

		SectionFragment fragment = new SectionFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_section, container, false);

		TextView textView = (TextView)view.findViewById(R.id.textview_section_index);
		textView.setText("Section : " + Integer.toString(getArguments().getInt(KEY_SECTION_INDEX)));

		return view;
	}
}
