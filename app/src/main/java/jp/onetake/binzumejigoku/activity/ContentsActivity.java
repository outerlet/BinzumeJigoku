package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.fragment.ContentsFragment;
import jp.onetake.binzumejigoku.fragment.dialog.AlertDialogFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.fragment.dialog.MultipleConfirmDialogFragment;

/**
 * ストーリーの進行を制御するアクティビティ
 */
public class ContentsActivity extends BasicActivity
		implements ContentsFragment.ContentsListener, AlertDialogFragment.OnAlertListener, ConfirmDialogFragment.OnConfirmListener {
	public static String KEY_SECTION_INDEX	= "ContentsActivity.KEY_SECTION_INDEX";

	private final String TAG_SECTION_FRAGMENT		= "ContentsActivity.TAG_SECTION_FRAGMENT";
	private final String TAG_DIALOG_SECTION			= "ContentsActivity.TAG_DIALOG_SECTION";
	private final String TAG_DIALOG_LAST_SECTION	= "ContentsActivity.TAG_DIALOG_LAST_SECTION";
	private final String TAG_DIALOG_BACKKEY			= "ContentsActivity.TAG_DIALOG_BACKKEY";

	private GestureDetectorCompat mGestureDetector;
	private int mSectionIndex;

	// 各種ジェスチャを捕捉するリスナオブジェクト
	private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			ContentsFragment fragment =
					(ContentsFragment)getSupportFragmentManager().findFragmentByTag(TAG_SECTION_FRAGMENT);
			fragment.advance();

			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			startActivity(new Intent(ContentsActivity.this, SaveActivity.class));
			overridePendingTransition(0, 0);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contents);

		mSectionIndex = getIntent().getIntExtra(KEY_SECTION_INDEX, -1);
		if (mSectionIndex == -1) {
			throw new UnsupportedOperationException(getString(R.string.exception_message_section_index));
		}

		mGestureDetector = new GestureDetectorCompat(this, mGestureListener);

		replaceFragment(mSectionIndex);
	}

	@Override
	public void onBackPressed() {
		ConfirmDialogFragment.newInstance(
				R.string.phrase_confirm,
				R.string.message_confirm_back,
				R.string.phrase_back,
				R.string.phrase_cancel).show(getSupportFragmentManager(), TAG_DIALOG_BACKKEY);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);

		return true;
	}

	@Override
	public void onSectionFinished() {
		if (mSectionIndex >= ContentsInterface.getInstance().getMaxSectionIndex()) {
			AlertDialogFragment.newInstance(
					R.string.phrase_confirm,
					R.string.message_last_section_finished,
					R.string.phrase_back).show(getSupportFragmentManager(), TAG_DIALOG_LAST_SECTION);
		} else {
			MultipleConfirmDialogFragment.newInstance(
					R.string.phrase_confirm,
					R.string.message_section_finished,
					R.string.phrase_next_section,
					R.string.phrase_back,
					R.string.phrase_finish_application).show(getSupportFragmentManager(), TAG_DIALOG_SECTION);
		}
	}

	@Override
	public void onConfirmed(DialogFragment dialog) {
		if (dialog.getTag().equals(TAG_DIALOG_LAST_SECTION)) {
			finish();
		}
	}

	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		// Backキーを押した時のダイアログ
		if (dialog.getTag().equals(TAG_DIALOG_BACKKEY)) {
			switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					finish();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.dismiss();
					break;
			}
		// 章が終わった時に表示されるダイアログ
		} else if (dialog.getTag().equals(TAG_DIALOG_SECTION)) {
			// 次の章へ
			if (which == DialogInterface.BUTTON_POSITIVE) {
				replaceFragment(++mSectionIndex);
			// 戻る
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				finish();
			// アプリを終了
			} else {
				finishApplication();
			}
		}
	}

	private void replaceFragment(int sectionIndex) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(
				R.id.layout_fragment_container, ContentsFragment.newInstance(sectionIndex), TAG_SECTION_FRAGMENT);
		transaction.commit();
	}
}
