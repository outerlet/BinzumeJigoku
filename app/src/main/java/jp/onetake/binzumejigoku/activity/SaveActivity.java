package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.SaveData;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.view.SaveButton;

/**
 * セーブ・ロードを行うことのできるアクティビティ
 */
public class SaveActivity extends BasicActivity implements View.OnClickListener, ConfirmDialogFragment.OnConfirmListener {
	/** セーブモードかどうかをIntentのExtraにセットするためのキー */
	public final static String EXTRA_SAVE_MODE	= "SaveActivity.EXTRA_SAVE_MODE";

	/** ロードしたとき呼び出し元のActivityにそのスロット番号を通知するためのキー */
	public final static String EXTRA_SLOT_INDEX = "SaveActivity.EXTRA_SLOT_INDEX";

	private final String TAG_DIALOG_SAVE	= "SaveActivity.TAG_DIALOG_SAVE";
	private final String TAG_DIALOG_LOAD	= "SaveActivity.TAG_DIALOG_LOAD";

	private LinearLayout mRootLayout;
	private TextView mModeView;
	private RelativeLayout mButtonLayout;

	private SaveData mTargetData;
	private boolean mIsSaveMode;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);

		mRootLayout = (LinearLayout)findViewById(R.id.layout_root);
		mModeView = (TextView)findViewById(R.id.textview_mode);
		mButtonLayout = (RelativeLayout)findViewById(R.id.layout_save_buttons);

		SaveButton button1 = (SaveButton)findViewById(R.id.button_save1);
		button1.setSaveData(ContentsInterface.getInstance().getSaveData(1));
		button1.setOnClickListener(this);

		SaveButton button2 = (SaveButton)findViewById(R.id.button_save2);
		button2.setSaveData(ContentsInterface.getInstance().getSaveData(2));
		button2.setOnClickListener(this);

		SaveButton button3 = (SaveButton)findViewById(R.id.button_save3);
		button3.setSaveData(ContentsInterface.getInstance().getSaveData(3));
		button3.setOnClickListener(this);

		mIsSaveMode = getIntent().getBooleanExtra(EXTRA_SAVE_MODE, true);

		ImageButton modeButton = (ImageButton)findViewById(R.id.imagebutton_change_mode);
		if (mIsSaveMode) {
			modeButton.setOnClickListener(this);
		} else {
			mModeView.setText(R.string.phrase_load);
			modeButton.setVisibility(View.INVISIBLE);
		}

		final float moveY = getResources().getDimensionPixelSize(R.dimen.save_button_move_y) * -1.0f;

		// 背景が透過付きの黒になったあと、ボタンがせり上がってくるアニメーション
		ObjectAnimator anim = ObjectAnimator.ofFloat(mRootLayout, "alpha", 0.0f, 1.0f);
		anim.setDuration(getResources().getInteger(R.integer.save_animation_duration_bgcolor));
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mButtonLayout.setVisibility(View.VISIBLE);

				AnimatorSet animSet = new AnimatorSet();
				animSet.playTogether(
						ObjectAnimator.ofFloat(mButtonLayout, "alpha", 0.0f, 1.0f),
						ObjectAnimator.ofFloat(mButtonLayout, "translationY", 0.0f, moveY));
				animSet.setDuration(getResources().getInteger(R.integer.save_animation_duration_move_y));
				animSet.start();
			}
		});
		anim.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onClick(View view) {
		// セーブスロット選択
		if (view.getId() != R.id.imagebutton_change_mode) {
			// なんかおかしな操作が行われた(ありえないボタン操作)
			if (view.getId() != R.id.button_save1 && view.getId() != R.id.button_save2 && view.getId() != R.id.button_save3) {
				throw new UnsupportedOperationException(getString(R.string.exception_unexpected_save_button_clicked));
			}

			mTargetData = ((SaveButton)view).getSaveData();

			// セーブ
			if (mIsSaveMode) {
				showDialogFragment(
						ConfirmDialogFragment.newInstance(R.string.phrase_confirm, R.string.message_save_confirmation),
						TAG_DIALOG_SAVE);
			// セーブされたデータがなければロードしない
			} else {
				if (mTargetData.hasSaved()) {
					showDialogFragment(
							ConfirmDialogFragment.newInstance(R.string.phrase_confirm, R.string.message_load_confirmation),
							TAG_DIALOG_LOAD);
				}
			}
		// セーブ・ロード切り替え
		} else {
			mIsSaveMode = !mIsSaveMode;
			mModeView.setText(mIsSaveMode ? R.string.phrase_save : R.string.phrase_load);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onConfirmed(DialogFragment dialog, int which) {
		if (which == DialogInterface.BUTTON_POSITIVE) {
			if (dialog.getTag().equals(TAG_DIALOG_SAVE)) {
				mTargetData.copyFrom(ContentsInterface.getInstance().getSaveData(0), false);
				mTargetData.setTimeMillis(System.currentTimeMillis());
				boolean success = mTargetData.save(this);

				Intent data = new Intent();
				data.putExtra(EXTRA_SAVE_MODE, true);
				data.putExtra(EXTRA_SLOT_INDEX, mTargetData.getSlotIndex());
				setResult(success ? RESULT_OK : RESULT_CANCELED, data);

				finish();
			} else if (dialog.getTag().equals(TAG_DIALOG_LOAD)) {
				Intent data = new Intent();
				data.putExtra(EXTRA_SAVE_MODE, false);
				data.putExtra(EXTRA_SLOT_INDEX, mTargetData.getSlotIndex());
				setResult(RESULT_OK, data);

				finish();
			}
		}
	}
}
