package jp.onetake.binzumejigoku.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.fragment.MainPageFragment;

public class MainPageAdapter extends FragmentPagerAdapter {
	private List<MainPageFragment> mFragmentList;

	public MainPageAdapter(FragmentManager fm) {
		super(fm);

		mFragmentList = new ArrayList<>();
	}

	public void addPage(String title, String summary, int bgResId) {
		mFragmentList.add(MainPageFragment.newInstance(mFragmentList.size(), title, summary, bgResId));
	}

	public void addPage(String title, String summary) {
		mFragmentList.add(MainPageFragment.newInstance(mFragmentList.size(), title, summary));
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
