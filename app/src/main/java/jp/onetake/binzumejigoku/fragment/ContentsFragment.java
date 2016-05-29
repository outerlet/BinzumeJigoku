package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.Image;
import jp.onetake.binzumejigoku.contents.element.SectionElement;
import jp.onetake.binzumejigoku.contents.element.Text;
import jp.onetake.binzumejigoku.contents.element.Title;
import jp.onetake.binzumejigoku.view.ContentsImageView;
import jp.onetake.binzumejigoku.view.ContentsTextView;
import jp.onetake.binzumejigoku.view.TimerView;
import jp.onetake.binzumejigoku.view.ContentsTitleView;

public class ContentsFragment extends Fragment
		implements TimerView.TimerListener, ContentsImageView.EffectListener {
	private ContentsImageView mImageView;
	private ContentsTitleView mTitleView;
	private ContentsTextView mTextView;

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

	public void advance(SectionElement element) {
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
		if (getActivity() instanceof SectionListener) {
			((SectionListener)getActivity()).onAdvanced();
		}
	}

	@Override
	public void onEffectFinished(ContentsImageView view) {
		if (getActivity() instanceof SectionListener) {
			((SectionListener)getActivity()).onAdvanced();
		}
	}

	/**
	 * セクションの進行が1つ進んだイベントを受け取るリスナ<br />
	 * このFragmentの呼び出し元であるActivityに実装する
	 */
	public interface SectionListener {
		/**
		 * セクションが1つ進んだときに呼び出される
		 */
		void onAdvanced();
	}
}
