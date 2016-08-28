package jp.onetake.binzumejigoku.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GestureDetectorCompat;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

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
	private float mFlingDistance;

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
		 * ロングタップでセーブ画面を開く
		 * {@inheritDoc}
		 */
		@Override
		public void onLongPress(MotionEvent e) {
			getFragment().requestSaveActivity();
		}

		/**
		 * 上向きのフリックでテキスト履歴を表示<br />
		 * 当初はバックキー長押しで実装していたがAndroid7.0(Nougat)でonKeyLongPressが検知されないのでこちらに変更
		 * {@inheritDoc}
		 */
		@Override
		public boolean onFling(MotionEvent upEvent, MotionEvent moveEvent, float velocityX, float velocityY) {
			if (upEvent.getY() - moveEvent.getY() > mFlingDistance) {
				getFragment().requestBacklogActivity();
			}
			return true;
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contents);

		mGestureDetector = new GestureDetectorCompat(this, mGestureListener);
		mFlingDistance = getResources().getDimensionPixelSize(R.dimen.fling_detect_action_y);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed() {
		ConfirmDialogFragment.newInstance(
				R.string.phrase_confirm,
				R.string.message_confirm_back,
				R.string.phrase_back,
				R.string.phrase_cancel).show(getSupportFragmentManager(), TAG_DIALOG_BACKKEY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mGestureDetector.onTouchEvent(event);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onLoadRequested(int slotIndex) {
		replaceFragmentBySaveData(ContentsInterface.getInstance().getSaveData(slotIndex));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfirmed(DialogFragment dialog) {
		if (dialog.getTag().equals(TAG_DIALOG_LAST_SECTION)) {
			finish();
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * 章を示すインデックス値を指定してフラグメントを生成・置換する
	 * @param sectionIndex	章を示すインデックス値
	 */
	private void replaceFragmentByIndex(int sectionIndex) {
		postFragment(
				R.id.layout_fragment_container,
				FragmentMethod.Replace,
				ContentsFragment.newInstance(sectionIndex),
				TAG_SECTION_FRAGMENT);
	}

	/**
	 * 特定のセーブデータをもとにフラグメントを生成・置換する
	 * @param saveData	セーブデータ
	 */
	private void replaceFragmentBySaveData(SaveData saveData) {
		mSectionIndex = saveData.getSectionIndex();

		postFragment(
				R.id.layout_fragment_container,
				FragmentMethod.Replace,
				ContentsFragment.newInstance(saveData),
				TAG_SECTION_FRAGMENT);
	}

	/**
	 * 物語を進めるためのフラグメントを取得する
	 * @return	フラグメント
	 */
	private ContentsFragment getFragment() {
		return (ContentsFragment)getSupportFragmentManager().findFragmentByTag(TAG_SECTION_FRAGMENT);
	}
}
