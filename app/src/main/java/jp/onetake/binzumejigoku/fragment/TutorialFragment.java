package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.view.FadeInTextView;

/**
 * チュートリアルの各ページに相当するFragment
 */
public class TutorialFragment extends BasicFragment {
	private static final String KEY_TUTORIAL_TITLE	= "TutorialFragment.KEY_TUTORIAL_TITLE";
	private static final String KEY_TUTORIAL_TEXT	= "TutorialFragment.KEY_TUTORIAL_TEXT";

	private TextView mTitleView;
	private FadeInTextView mTextView;

	/**
	 * Fragmentを初期化するために必要なパラメータを与えてインスタンスを生成する
	 * @param title		タイトル文字列
	 * @param textList	表示するテキストを行ごとに格納したリスト
	 * @return	このクラスのインスタンス
	 */
	public static TutorialFragment newInstance(String title, ArrayList<String> textList) {
		Bundle params = new Bundle();
		params.putString(KEY_TUTORIAL_TITLE, title);
		params.putStringArrayList(KEY_TUTORIAL_TEXT, textList);

		TutorialFragment fragment = new TutorialFragment();
		fragment.setArguments(params);
		return fragment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_tutorial, container, false);

		mTitleView = (TextView)view.findViewById(R.id.textview_tutorial_title);
		mTextView = (FadeInTextView)view.findViewById(R.id.textview_tutorial_text);

		return view;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		mTitleView.setText(getArguments().getString(KEY_TUTORIAL_TITLE));

		for (String text : getArguments().getStringArrayList(KEY_TUTORIAL_TEXT)) {
			mTextView.addText(text);
		}
	}

	/**
	 * ページが表示された時のアニメーションを開始する
	 * @param delay	アニメーションの開始前に遅延させるか.すぐ開始するならfalse
	 */
	public void startAnimation(boolean delay) {
		mTextView.start(false, delay ? getResources().getInteger(R.integer.fadein_textview_animation_delay) : 0);
	}

	/**
	 * タイトル文字列のフェードが行われた(正確には「開始した」)かどうか
	 * @return	フェードが行われた後ならtrue
	 */
	public boolean isFadeFinished() {
		return mTextView.isFadeFinished();
	}
}
