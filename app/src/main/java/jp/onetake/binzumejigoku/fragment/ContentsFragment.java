package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsHolder;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.view.ContentsImageView;
import jp.onetake.binzumejigoku.view.ContentsTextView;
import jp.onetake.binzumejigoku.view.ContentsTitleView;
import jp.onetake.binzumejigoku.view.TimerView;

public class ContentsFragment extends Fragment
		implements TimerView.TimerListener, ContentsImageView.EffectListener {
	public static final String KEY_SECTION_INDEX	= "ContentsFragment.KEY_SECTION_INDEX";

	private ContentsImageView mImageView;
	private ContentsTitleView mTitleView;
	private ContentsTextView mTextView;

	private ContentsHolder mHolder;
	private boolean mIsOngoing;

	public static ContentsFragment newInstance(int sectionIndex) {
		Bundle params = new Bundle();
		params.putInt(KEY_SECTION_INDEX, sectionIndex);

		ContentsFragment fragment = new ContentsFragment();
		fragment.setArguments(params);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_contents, container, false);

		mImageView = (ContentsImageView)view.findViewById(R.id.imageview_contents);
		mImageView.setListener(this);

		mTitleView = (ContentsTitleView)view.findViewById(R.id.titleview_title);
		mTitleView.setListener(this);

		mTextView = (ContentsTextView)view.findViewById(R.id.textview_contents);
		mTextView.setListener(this);

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mHolder = new ContentsHolder(getActivity(), getArguments().getInt(KEY_SECTION_INDEX));
		mIsOngoing = false;
	}

	public void advance() {
		if (!mIsOngoing) {
			if (mHolder.hasNext()) {
				SectionElement element = mHolder.next();

				mIsOngoing = (element instanceof Title || element instanceof Text || element instanceof Image);

				switch (element.getContentsType()) {
					case Title:
						mTitleView.setTitle((Title)element);
						mTitleView.setVisibility(View.VISIBLE);
						mTitleView.start();
						break;
					case Text:
						mTextView.setText((Text)element);
						mTextView.start();
						break;
					case ClearText:
						mTextView.clear();
						break;
					case Image:
						mImageView.setImage((Image)element);
						mImageView.start();
						break;
					default:
						// Do nothing.
				}

				// "chain"属性が"immediate"なら更に進める
				if (element.getChainType() == SectionElement.ChainType.Immediate) {
					advance();
				}
			} else {
				if (getActivity() instanceof ContentsListener) {
					((ContentsListener)getActivity()).onSectionFinished();
				}
			}
		}
	}

	private void advanceIfChained() {
		mIsOngoing = false;

		if (mHolder.current().getChainType() == SectionElement.ChainType.Wait) {
			advance();
		}
	}

	@Override
	public void onStarted(TimerView view) {
		if (!(view instanceof ContentsTitleView) && mTitleView.getVisibility() == View.VISIBLE) {
			mTitleView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onPeriod(TimerView view) {
		// Do nothing.
	}

	@Override
	public void onStopped(TimerView view) {
		advanceIfChained();
	}

	@Override
	public void onEffectFinished(ContentsImageView view) {
		advanceIfChained();
	}

	public interface ContentsListener {
		void onSectionFinished();
	}
}
