package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.fragment.MainPageFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.view.MainPageAdapter;

/**
 * メイン画面
 * <ol>
 *     <li>進行させる章を選択</li>
 *     <li>ロード画面を開く</li>
 * </ol>
 */
public class MainActivity extends BasicActivity
		implements MainPageFragment.PageEventListener, ConfirmDialogFragment.OnConfirmListener {
	public static final String INTENT_KEY_FINISH_APP	= "MainActivity.INTENT_KEY_FINISH_APP";

	// バックキーをこのミリ秒数以内に2回押したらアプリを終了
	private final long BACKKEY_FINISH_MILLIS = 2000;

	private int mBackPressCount;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getIntent().getBooleanExtra(INTENT_KEY_FINISH_APP, false)) {
			finish();
		} else {
			setContentView(R.layout.activity_main);

			String[] titles = getResources().getStringArray(R.array.array_section_titles);
			String[] summaries = getResources().getStringArray(R.array.array_section_summarys);
			TypedArray array = getResources().obtainTypedArray(R.array.array_section_drawables);
			if (titles.length != summaries.length) {
				throw new IllegalStateException(getString(R.string.exception_illegal_section_definition));
			}

			MainPageAdapter adapter = new MainPageAdapter(getSupportFragmentManager());
			for (int i = 0 ; i < titles.length ; i++) {
				if (i < array.length()) {
					adapter.addPage(titles[i], summaries[i], array.getResourceId(i, 0));
				} else {
					adapter.addPage(titles[i], summaries[i]);
				}
			}

			ViewPager viewPager = (ViewPager)findViewById(R.id.vpager_page);
			viewPager.setAdapter(adapter);

			TabLayout tabLayout = (TabLayout)findViewById(R.id.layout_tab);
			tabLayout.setupWithViewPager(viewPager);

			mBackPressCount = 0;
		}
	}

	@Override
	public void onBackPressed() {
		if (mBackPressCount > 0) {
			finish();
		} else {
			++mBackPressCount;

			Toast.makeText(this, R.string.message_confirm_finish_application, Toast.LENGTH_LONG).show();

			// 一定時間(BACKKEY_FINISH_MILLISに定義されているミリ秒)のうちにバックキーを2回押したら終了
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Thread.sleep(BACKKEY_FINISH_MILLIS);
					} catch (InterruptedException ie) {}

					mBackPressCount = 0;
				}
			}).start();
		}
	}

	/**
	 * <p>この画面はアクションバーを表示するのでtrueを返却するよう実装</p>
	 * {@inheritDoc}
	 */
	@Override
	protected boolean shouldActionBarShown() {
		return true;
	}

	@Override
	public void onPageSelected(int pageIndex) {
		Intent intent = new Intent(this, ContentsActivity.class);
		intent.putExtra(ContentsActivity.KEY_SECTION_INDEX, pageIndex);
		startActivity(intent);
	}

	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			finish();
		}
	}
}
