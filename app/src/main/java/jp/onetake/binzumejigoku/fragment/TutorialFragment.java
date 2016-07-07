package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.view.FadeInTextView;

public class TutorialFragment extends Fragment {
	private static final String KEY_TUTORIAL_TITLE	= "TutorialFragment.KEY_TUTORIAL_TITLE";
	private static final String KEY_TUTORIAL_TEXT	= "TutorialFragment.KEY_TUTORIAL_TEXT";

	private TextView mTitleView;
	private FadeInTextView mTextView;

	public static TutorialFragment newInstance(String title, ArrayList<String> textList) {
		Bundle params = new Bundle();
		params.putString(KEY_TUTORIAL_TITLE, title);
		params.putStringArrayList(KEY_TUTORIAL_TEXT, textList);

		TutorialFragment fragment = new TutorialFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

		mTitleView = (TextView)view.findViewById(R.id.textview_tutorial_title);
		mTextView = (FadeInTextView)view.findViewById(R.id.textview_tutorial_text);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mTitleView.setText(getArguments().getString(KEY_TUTORIAL_TITLE));

		for (String text : getArguments().getStringArrayList(KEY_TUTORIAL_TEXT)) {
			mTextView.addText(text);
		}
	}

	public void startAnimation(boolean delay) {
		mTextView.start(false, delay ? getResources().getInteger(R.integer.fadein_textview_animation_delay) : 0);
	}

	public boolean hasFaded() {
		return mTextView.hasFaded();
	}
}
