package jp.onetake.binzumejigoku.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.fragment.MainFragment;

public class MainPagerAdapter extends FragmentPagerAdapter {
	private List<MainFragment> mFragmentList;

	public MainPagerAdapter(FragmentManager fm) {
		super(fm);

		mFragmentList = new ArrayList<>();
	}

	public void addPage(String title, String summary, boolean showTitle) {
		mFragmentList.add(MainFragment.newInstance(mFragmentList.size(), title, summary, showTitle));
	}

	public void addPage(String title, String summary, boolean showTitle, int bgResId) {
		mFragmentList.add(MainFragment.newInstance(mFragmentList.size(), title, summary, showTitle, bgResId));
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return mFragmentList.get(position).getTitle();
	}
}
