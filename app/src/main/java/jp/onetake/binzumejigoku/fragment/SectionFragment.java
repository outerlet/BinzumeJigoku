package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.parser.ContentsDbParser;
import jp.onetake.binzumejigoku.util.Utility;
import jp.onetake.binzumejigoku.view.StreamTextView;
import jp.onetake.binzumejigoku.view.TimerView;

public class SectionFragment extends Fragment implements TimerView.TimerListener, View.OnTouchListener {
	private final static String KEY_SECTION_INDEX = "SectionFragment.KEY_SECTION_INDEX";

	private StreamTextView mTextView;
	private ContentsHolder mHolder;

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

		mTextView = (StreamTextView)view.findViewById(R.id.textview_stream);
		mTextView.setListener(this);

		view.findViewById(R.id.view_accept_touch).setOnTouchListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mHolder = (new ContentsDbParser(getContext())).parse(getArguments().getInt(KEY_SECTION_INDEX));
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		Utility.printLog("Fragment", "onTouch");

		if (event.getAction() == MotionEvent.ACTION_UP && mHolder.hasNext()) {
			SectionElement elm = mHolder.next();

			if (elm instanceof Text) {
				mTextView.setText((Text)elm);
				mTextView.start();
			}
		}

		return true;
	}

	@Override
	public void onStarted() {
		Utility.printLog("Fragment", "onStarted");
	}

	@Override
	public void onPeriod() {
		Utility.printLog("Fragment", "onPeriod");
	}

	@Override
	public void onStopped() {
		Utility.printLog("Fragment", "onStopped");
	}
}
