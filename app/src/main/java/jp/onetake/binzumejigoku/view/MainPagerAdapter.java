package jp.onetake.binzumejigoku.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.fragment.MainFragment;

/**
 * MainActivityでセクションの概要を表示させるViewPagerに適用されるAdapter
 */
public class MainPagerAdapter extends FragmentPagerAdapter {
	private List<MainFragment> mFragmentList;

	/**
	 * コンストラクタ
	 * @param fm	FragmentManager
	 */
	public MainPagerAdapter(FragmentManager fm) {
		super(fm);

		mFragmentList = new ArrayList<>();
	}

	/**
	 * セクションの概要を説明するためのページを追加する
	 * @param title		セクションのタイトル
	 * @param summary	セクションの概要を説明するテキスト
	 * @param showTitle	タイトルを表示するか.trueなら表示
	 */
	public void addPage(String title, String summary, boolean showTitle) {
		mFragmentList.add(MainFragment.newInstance(mFragmentList.size(), title, summary, showTitle));
	}

	/**
	 * セクションの概要を説明するためのページを追加する
	 * @param title		セクションのタイトル
	 * @param summary	セクションの概要を説明するテキスト
	 * @param showTitle	タイトルを表示するか.trueなら表示
	 * @param bgResId	ページの背景画像に対応するdrawableリソースのID
	 */
	public void addPage(String title, String summary, boolean showTitle, int bgResId) {
		mFragmentList.add(MainFragment.newInstance(mFragmentList.size(), title, summary, showTitle, bgResId));
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
	 * {@inheritDoc}
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentList.get(position).getTitle();
	}
}
