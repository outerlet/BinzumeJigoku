package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.SaveData;
import jp.onetake.binzumejigoku.fragment.ContentsFragment;
import jp.onetake.binzumejigoku.fragment.dialog.AlertDialogFragment;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.fragment.dialog.MultipleConfirmDialogFragment;

/**
 * ストーリーの進行を制御するアクティビティ
 */
public class ContentsActivity extends BasicActivity
		implements ContentsFragment.ContentsEventListener, AlertDialogFragment.OnAlertListener, ConfirmDialogFragment.OnConfirmListener {
	public static String KEY_SECTION_INDEX	= "ContentsActivity.KEY_SECTION_INDEX";
	public static String KEY_SAVE_DATA		= "ContentsActivity.KEY_SAVE_DATA";

	private final String TAG_SECTION_FRAGMENT		= "ContentsActivity.TAG_SECTION_FRAGMENT";
	private final String TAG_DIALOG_SECTION			= "ContentsActivity.TAG_DIALOG_SECTION";
	private final String TAG_DIALOG_LAST_SECTION	= "ContentsActivity.TAG_DIALOG_LAST_SECTION";
	private final String TAG_DIALOG_BACKKEY			= "ContentsActivity.TAG_DIALOG_BACKKEY";

	private GestureDetectorCompat mGestureDetector;
	private int mSectionIndex;
	private SaveData mPendingSaveData;

	// 各種ジェスチャを捕捉するリスナオブジェクト
	private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
		/**
		 * シングルタップ。話を進行させる
		 * {@inheritDoc}
		 */
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			getFragment().advance();
			return true;
		}

		/**
		 * 長時間タップ。セーブ画面を開く
		 * {@inheritDoc}
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			startActivityForResult(
					new Intent(ContentsActivity.this, SaveActivity.class),
					getResources().getInteger(R.integer.request_code_save_activity));
			overridePendingTransition(0, 0);
		}

		/**
		 * フリック。上方向でバックログ、下方向で早送り
		 * {@inheritDoc}
		 */
		@Override
		public boolean onFling(MotionEvent upEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
			// TODO:フリック操作実装
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_contents);

		mGestureDetector = new GestureDetectorCompat(this, mGestureListener);

		// SaveDataが送られてきたらロードしての開始、さもなければ指定された章を最初から
		// どっちも送られてこなかった場合は不正な呼び出しとしてエラー
		SaveData saveData = (SaveData)getIntent().getSerializableExtra(KEY_SAVE_DATA);
		if (saveData != null) {
			replaceFragmentBySaveData(saveData);
		} else {
			mSectionIndex = getIntent().getIntExtra(KEY_SECTION_INDEX, -1);
			if (mSectionIndex == -1) {
				throw new UnsupportedOperationException(getString(R.string.exception_message_section_index));
			}

			replaceFragmentByIndex(mSectionIndex);
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();

		if (mPendingSaveData != null) {
			replaceFragmentBySaveData(mPendingSaveData);
			mPendingSaveData = null;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_save_activity)) {
			// セーブもしくはロードした場合はEXTRA_SAVE_MODEにboolean値が入っている
			if (data != null && data.hasExtra(SaveActivity.EXTRA_SAVE_MODE)) {
				boolean isSave = data.getBooleanExtra(SaveActivity.EXTRA_SAVE_MODE, false);

				if (isSave) {
					int msgId = (resultCode == RESULT_OK) ? R.string.message_save_successful : R.string.message_save_failure;
					Toast.makeText(this, msgId, Toast.LENGTH_SHORT).show();
				} else {
					int slotIndex = data.getIntExtra(SaveActivity.EXTRA_SLOT_INDEX, -1);

					if (slotIndex != -1) {
						// SaveActivityから帰ってきた直後にフラグメントの操作をするとIllegalStateExceptionが
						// 発生するので、一旦フィールドに放り込んでおいてonPostResumeで操作する
						mPendingSaveData = ContentsInterface.getInstance().getSaveData(slotIndex);
					} else {
						Toast.makeText(this, R.string.message_load_failure, Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
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
				replaceFragmentByIndex(++mSectionIndex);
			// 戻る
			} else if (which == DialogInterface.BUTTON_NEGATIVE) {
				finish();
			// アプリを終了
			} else {
				finishApplication();
			}
		}
	}

	private void replaceFragmentByIndex(int sectionIndex) {
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(
				R.id.layout_fragment_container, ContentsFragment.newInstance(sectionIndex), TAG_SECTION_FRAGMENT);
		transaction.commit();
	}

	private void replaceFragmentBySaveData(SaveData saveData) {
		mSectionIndex = saveData.getSectionIndex();

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
		transaction.replace(
				R.id.layout_fragment_container, ContentsFragment.newInstance(saveData), TAG_SECTION_FRAGMENT);
		transaction.commit();
	}

	private ContentsFragment getFragment() {
		return (ContentsFragment)getSupportFragmentManager().findFragmentByTag(TAG_SECTION_FRAGMENT);
	}
}
