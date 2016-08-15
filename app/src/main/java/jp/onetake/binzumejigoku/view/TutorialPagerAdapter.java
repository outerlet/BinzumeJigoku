package jp.onetake.binzumejigoku.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.fragment.TutorialFragment;

/**
 * TutorialActivityでチュートリアルの内容をページで表示させるViewPagerに適用されるAdapter
 */
public class TutorialPagerAdapter extends FragmentPagerAdapter {
	private List<TutorialFragment> mFragmentList;

	/**
	 * コンストラクタ
	 * @param manager	FragmentManager
	 */
	public TutorialPagerAdapter(FragmentManager manager) {
		super(manager);

		mFragmentList = new ArrayList<>();
	}

	/**
	 * チュートリアルのページを追加する
	 * @param title		ページのタイトル
	 * @param textList	ページに表示させるテキストを格納したリスト
	 */
	public void addPage(String title, ArrayList<String> textList) {
		mFragmentList.add(TutorialFragment.newInstance(title, textList));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	/**
	 * チュートリアルの表示アニメーションを実行する
	 * @param position	アニメーションを行うページを指すインデックス値
	 * @param delay		アニメーションを開始するまでに遅延を設けるかどうか.trueなら遅延させる
	 */
	public void startAnimation(int position, boolean delay) {
		TutorialFragment fragment = mFragmentList.get(position);
		if (!fragment.isFadeFinished()) {
			fragment.startAnimation(delay);
		}
	}
}
