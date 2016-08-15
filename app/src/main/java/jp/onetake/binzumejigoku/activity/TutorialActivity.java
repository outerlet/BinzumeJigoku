package jp.onetake.binzumejigoku.activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.ArrayList;
import java.util.Arrays;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.view.PagerIndicatorView;
import jp.onetake.binzumejigoku.view.TutorialPagerAdapter;

/**
 * チュートリアルを実行するアクティビティ
 */
public class TutorialActivity extends BasicActivity {
	private ViewPager mViewPager;
	private PagerIndicatorView mPagerIndicator;

	// ページ切り替えをしたときのイベントを捕捉するリスナ
	// ・初めて表示したページでアニメーションを発生させる
	// ・ページのインジケータを切り替える
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		private boolean mmFirstAnimation = false;

		@Override
		public void onPageSelected(int position) {
			((TutorialPagerAdapter)mViewPager.getAdapter()).startAnimation(position, false);

			mPagerIndicator.setActiveIndex(position);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// アクティビティが最初に表示されたとき。1ページ目のアニメーションを開始しインジケータを1つめに合わせる
			if (position == 0 && positionOffset == 0.0f && positionOffsetPixels == 0 && !mmFirstAnimation) {
				((TutorialPagerAdapter)mViewPager.getAdapter()).startAnimation(position, true);
				mmFirstAnimation = true;
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			// Do nothing.
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		TutorialPagerAdapter adapter = new TutorialPagerAdapter(getSupportFragmentManager());

		for (int i = 1 ; i <= 3 ; i++) {
			int titleId = getResources().getIdentifier(String.format("title_tutorial_page%d", i), "string", getPackageName());
			String title = getResources().getString(titleId);

			int textsId = getResources().getIdentifier(String.format("tutorial_texts_page%d", i), "array", getPackageName());
			ArrayList<String> textList = new ArrayList<>(Arrays.asList(getResources().getStringArray(textsId)));

			adapter.addPage(title, textList);
		}

		mViewPager = (ViewPager)findViewById(R.id.viewpager_tutorial);
		mViewPager.setOffscreenPageLimit(adapter.getCount());
		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(mPageChangeListener);

		mPagerIndicator = (PagerIndicatorView)findViewById(R.id.pager_indicator_tutorial);
		mPagerIndicator.setPageCount(3);
		mPagerIndicator.setActiveIndex(0);
	}
}
