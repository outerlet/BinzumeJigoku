package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.fragment.MainFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.view.MainFragmentPagerAdapter;

/**
 * メイン画面<br />
 * 以下のアクションを行うことができる
 * <ol>
 *     <li>進行させる章を選択</li>
 *     <li>ロード画面を開く</li>
 * </ol>
 */
public class MainActivity extends BasicActivity
		implements MainFragment.SectionSelectListener, ConfirmDialogFragment.OnConfirmListener {
	/** アプリケーションを終了させるためのインテントを発行する際Extraにセットするキー */
	public static final String INTENT_KEY_FINISH_APP	= "MainActivity.INTENT_KEY_FINISH_APP";

	private final String TAG_DIALOG_LOAD_LATEST	= "MainActivity.TAG_DIALOG_LOAD_LATEST";

	// バックキーをこのミリ秒数以内に2回押したらアプリを終了
	private final long BACKKEY_FINISH_MILLIS = 2000;

	private ViewPager mViewPager;
	private ImageView mIndicatorLeft;
	private ImageView mIndicatorRight;

	private int mBackPressCount;

	/**
	 * 章を選択するときに使うViewPagerに関わるイベントを捕捉するリスナ
	 */
	private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
		@Override
		public void onPageSelected(int position) {
			mIndicatorLeft.setVisibility((position == 0) ? View.INVISIBLE : View.VISIBLE);
			mIndicatorRight.setVisibility(
					(position == mViewPager.getAdapter().getCount() - 1) ? View.INVISIBLE : View.VISIBLE);
		}

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			// Do nothing.
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

		if (getIntent().getBooleanExtra(INTENT_KEY_FINISH_APP, false)) {
			finish();
			return;
		}

		setContentView(R.layout.activity_main);

		setSupportActionBar((Toolbar)findViewById(R.id.toolbar));

		mIndicatorLeft = (ImageView)findViewById(R.id.imageview_indicator_left);
		mIndicatorRight = (ImageView)findViewById(R.id.imageview_indicator_right);

		String[] titles = getResources().getStringArray(R.array.array_section_titles);
		String[] summaries = getResources().getStringArray(R.array.array_section_summarys);
		TypedArray typedArray = getResources().obtainTypedArray(R.array.array_section_drawables);
		if (titles.length != summaries.length) {
			throw new IllegalStateException(getString(R.string.exception_illegal_section_definition));
		}

		MainFragmentPagerAdapter adapter = new MainFragmentPagerAdapter(getSupportFragmentManager());
		for (int i = 0 ; i < titles.length ; i++) {
			if (i < typedArray.length()) {
				adapter.addPage(titles[i], summaries[i], true, typedArray.getResourceId(i, 0));
			} else {
				adapter.addPage(titles[i], summaries[i], true);
			}
		}

		typedArray.recycle();

		mViewPager = (ViewPager)findViewById(R.id.viewpager_section);
		mViewPager.setAdapter(adapter);
		mViewPager.addOnPageChangeListener(mPageChangeListener);

		mBackPressCount = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onStop() {
		super.onStop();

		mViewPager.removeOnPageChangeListener(mPageChangeListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_save_activity)) {
			if (resultCode == RESULT_OK && data != null && !data.getBooleanExtra(SaveActivity.EXTRA_SAVE_MODE, true)) {
				int slotIndex = data.getIntExtra(SaveActivity.EXTRA_SLOT_INDEX, -1);

				if (slotIndex != -1) {
					Intent intent = new Intent(this, ContentsActivity.class);
					intent.putExtra(ContentsActivity.KEY_SAVE_DATA, ContentsInterface.getInstance().getSaveData(slotIndex));
					startActivity(intent);
				} else {
					Toast.makeText(this, R.string.message_load_failure, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
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
	 * {@inheritDoc}
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_load_latest) {
			ConfirmDialogFragment
					.newInstance(R.string.phrase_confirm, R.string.message_load_confirmation_latest)
					.show(getSupportFragmentManager(), TAG_DIALOG_LOAD_LATEST);
		} else if (item.getItemId() == R.id.menu_load) {
			Intent intent = new Intent(this, SaveActivity.class);
			intent.putExtra(SaveActivity.EXTRA_SAVE_MODE, false);
			startActivityForResult(intent, getResources().getInteger(R.integer.request_code_save_activity));
		} else if (item.getItemId() == R.id.menu_setting) {
			startActivity(new Intent(this, SettingActivity.class));
		}

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onSectionSelected(int sectionIndex) {
		Intent intent = new Intent(this, ContentsActivity.class);
		intent.putExtra(ContentsActivity.KEY_SECTION_INDEX, sectionIndex);
		startActivity(intent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		if (dialog.getTag().equals(TAG_DIALOG_LOAD_LATEST)) {
			if (which == DialogInterface.BUTTON_POSITIVE) {
				Intent intent = new Intent(this, ContentsActivity.class);
				intent.putExtra(ContentsActivity.KEY_SAVE_DATA, ContentsInterface.getInstance().getSaveData(0));
				startActivity(intent);
			}
		}
	}
}
