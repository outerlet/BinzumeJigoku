package jp.onetake.binzumejigoku.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.fragment.TutorialFragment;

public class TutorialPagerAdapter extends FragmentPagerAdapter {
	private List<TutorialFragment> mFragmentList;

	public TutorialPagerAdapter(FragmentManager manager) {
		super(manager);

		mFragmentList = new ArrayList<>();
	}

	public void addPage(String title, ArrayList<String> textList) {
		mFragmentList.add(TutorialFragment.newInstance(title, textList));
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get(position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size();
	}

	public void startAnimation(int position, boolean delay) {
		TutorialFragment fragment = mFragmentList.get(position);
		if (!fragment.hasFaded()) {
			fragment.startAnimation(delay);
		}
	}
}
