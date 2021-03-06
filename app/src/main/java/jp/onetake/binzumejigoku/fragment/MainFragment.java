package jp.onetake.binzumejigoku.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;

/**
 * MainActivityで表示するViewPagerの各ページに相当するFragment
 */
public class MainFragment extends BasicFragment {
	/**
	 * セクションを選択したときのイベントを捕捉するためにActivityに実装するリスナ
	 */
	public interface SectionSelectListener {
		/**
		 * セクションを選択したイベントを捕捉する
		 * @param sectionIndex	選択したセクションのインデックス
		 */
		void onSectionSelected(int sectionIndex);
	}

	private static final String KEY_SECTION_INDEX		= "MainFragment.KEY_SECTION_INDEX";
	private static final String KEY_TITLE				= "MainFragment.KEY_TITLE";
	private static final String KEY_SUMMARY				= "MainFragment.KEY_SUMMARY";
	private static final String KEY_SHOW_TITLE			= "MainFragment.KEY_SHOW_TITLE";
	private static final String KEY_BACKGROUND_RESID	= "MainFragment.KEY_BACKGROUND_RESID";
	private static final int UNKNOWN_DRAWABLE_RESID		= -1;

	/**
	 * このFragmentを初期化するために必要なパラメータを与えてインスタンスを生成する
	 * @param sectionIndex	セクションインデックス
	 * @param title			タイトル文字列
	 * @param summary		セクションの概要
	 * @param showTitle		タイトルを表示するかしないか.するならtrue
	 * @return	このクラスのインスタンス
	 */
	public static MainFragment newInstance(int sectionIndex, String title, String summary, boolean showTitle) {
		return newInstance(sectionIndex, title, summary, showTitle, UNKNOWN_DRAWABLE_RESID);
	}

	/**
	 * このFragmentを初期化するために必要なパラメータを与えてインスタンスを生成する
	 * @param sectionIndex	セクションインデックス
	 * @param title			タイトル文字列
	 * @param summary		セクションの概要
	 * @param showTitle		タイトルを表示するかしないか.するならtrue
	 * @param bgResId		背景画像のdrawableリソースID
	 * @return	このクラスのインスタンス
	 */
	public static MainFragment newInstance(int sectionIndex, String title, String summary, boolean showTitle, int bgResId) {
		Bundle params = new Bundle();
		params.putInt(KEY_SECTION_INDEX, sectionIndex);
		params.putString(KEY_TITLE, title);
		params.putBoolean(KEY_SHOW_TITLE, showTitle);
		params.putString(KEY_SUMMARY, summary);

		if (bgResId != UNKNOWN_DRAWABLE_RESID) {
			params.putInt(KEY_BACKGROUND_RESID, bgResId);
		}

		MainFragment fragment = new MainFragment();
		fragment.setArguments(params);
		return fragment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_main, container, false);

		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (getActivity() instanceof SectionSelectListener) {
					((SectionSelectListener)getActivity()).onSectionSelected(getSectionIndex());
				}
			}
		});

		TextView titleView = (TextView)view.findViewById(R.id.textview_section_title);
		titleView.setText(getArguments().getString(KEY_TITLE));
		titleView.setVisibility(getArguments().getBoolean(KEY_SHOW_TITLE) ? View.VISIBLE : View.INVISIBLE);

		TextView textView = (TextView)view.findViewById(R.id.textview_section_summary);
		textView.setText(getArguments().getString(KEY_SUMMARY));

		ImageView imageView = (ImageView)view.findViewById(R.id.imageview_background);
		if (getArguments().containsKey(KEY_BACKGROUND_RESID)) {
			imageView.setBackgroundResource(getArguments().getInt(KEY_BACKGROUND_RESID));
		} else {
			imageView.setVisibility(View.INVISIBLE);
		}

		return view;
	}

	/**
	 * セクション番号を返却する
	 * @return	セクション番号
	 */
	public int getSectionIndex() {
		return getArguments().getInt(KEY_SECTION_INDEX);
	}

	/**
	 * タイトルを返却する
	 * @return	タイトル
	 */
	public String getTitle() {
		return getArguments().getString(KEY_TITLE);
	}
}
