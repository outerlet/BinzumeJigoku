package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.element.ClearText;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.view.RubyTextView;
import jp.onetake.binzumejigoku.view.TimerView;
import jp.onetake.binzumejigoku.view.TitleTextView;

public class SectionFragment extends Fragment implements TimerView.TimerListener, View.OnTouchListener {
	private final static String KEY_SECTION_INDEX = "SectionFragment.KEY_SECTION_INDEX";

	private TitleTextView mTitleView;
	private RubyTextView mTextView;

	private ContentsHolder mHolder;
	private boolean mIsOngoing;

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

		mTitleView = (TitleTextView)view.findViewById(R.id.titleview_title);
		mTitleView.setListener(this);

		mTextView = (RubyTextView)view.findViewById(R.id.rubyview_contents);
		mTextView.setListener(this);

		view.findViewById(R.id.view_accept_touch).setOnTouchListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mHolder = new ContentsHolder(getContext(), getArguments().getInt(KEY_SECTION_INDEX));
		mIsOngoing = false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP && mHolder.hasNext() && !mIsOngoing) {
			SectionElement elm = mHolder.next();

			mIsOngoing = (elm instanceof Title || elm instanceof Text || elm instanceof ClearText);

			switch (elm.getContentsType()) {
				case Title:
					mTitleView.setTitle((Title)elm);
					mTitleView.setVisibility(View.VISIBLE);
					mTitleView.start();
					break;
				case Text:
					mTextView.setText((Text)elm);
					mTextView.start();
					break;
				case ClearText:
					mTextView.clear();
					mTextView.start();
					break;
				case Image:
					break;
				default:
					// Do nothing.
			}
		}

		return true;
	}

	@Override
	public void onStarted(TimerView view) {
		if (!(view instanceof TitleTextView) && mTitleView.getVisibility() == View.VISIBLE) {
			mTitleView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPeriod(TimerView view) {
	}

	@Override
	public void onStopped(TimerView view) {
		mIsOngoing = false;
	}
}
