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
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.common.SaveData;
import jp.onetake.binzumejigoku.fragment.dialog.ConfirmDialogFragment;
import jp.onetake.binzumejigoku.view.SaveButton;

public class SaveActivity extends BasicActivity
		implements View.OnClickListener, ObjectAnimator.AnimatorListener, ConfirmDialogFragment.OnConfirmListener {
	public final static String EXTRA_SAVE_MODE	= "SaveActivity.EXTRA_SAVE_MODE";
	public final static String EXTRA_SLOT_INDEX = "SaveActivity.EXTRA_SLOT_INDEX";

	private final String TAG_DIALOG_SAVE	= "SaveActivity.TAG_DIALOG_SAVE";
	private final String TAG_DIALOG_LOAD	= "SaveActivity.TAG_DIALOG_LOAD";

	private final int ANIMATION_DURATION_BGCOLOR	= 300;
	private final int ANIMATION_DURATION_MOVE_Y		= 300;

	private LinearLayout mRootLayout;
	private TextView mModeView;
	private RelativeLayout mButtonLayout;

	private SaveData mTargetData;
	private boolean mIsSaveMode;

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
		anim.setDuration(ANIMATION_DURATION_BGCOLOR);
		anim.addListener(new AnimatorListenerAdapter() {
			@Override
			public void onAnimationEnd(Animator animation) {
				mButtonLayout.setVisibility(View.VISIBLE);

				AnimatorSet animSet = new AnimatorSet();
				animSet.playTogether(
						ObjectAnimator.ofFloat(mButtonLayout, "alpha", 0.0f, 1.0f),
						ObjectAnimator.ofFloat(mButtonLayout, "translationY", 0.0f, moveY));
				animSet.setDuration(ANIMATION_DURATION_MOVE_Y);
				animSet.addListener(SaveActivity.this);
				animSet.start();
			}
		});
		anim.start();
	}

	@Override
	public void onClick(View view) {
		// セーブスロット選択
		if (view.getId() != R.id.imagebutton_change_mode) {
			switch (view.getId()) {
				case R.id.button_save1:
					mTargetData = ContentsInterface.getInstance().getSaveData(1);
					break;
				case R.id.button_save2:
					mTargetData = ContentsInterface.getInstance().getSaveData(2);
					break;
				case R.id.button_save3:
					mTargetData = ContentsInterface.getInstance().getSaveData(3);
					break;
				default:
					Toast.makeText(this, R.string.message_save_successful, Toast.LENGTH_LONG).show();
					return;
			}

			if (mIsSaveMode) {
				ConfirmDialogFragment
						.newInstance(R.string.phrase_confirm, R.string.message_save_confirmation)
						.show(getSupportFragmentManager(), TAG_DIALOG_SAVE);
			} else {
				ConfirmDialogFragment
						.newInstance(R.string.phrase_confirm, R.string.message_load_confirmation)
						.show(getSupportFragmentManager(), TAG_DIALOG_LOAD);
			}
		// セーブ・ロード切り替え
		} else {
			mIsSaveMode = !mIsSaveMode;
			mModeView.setText(mIsSaveMode ? R.string.phrase_save : R.string.phrase_load);
		}
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (animation instanceof ObjectAnimator) {
			if (((ObjectAnimator)animation).getTarget() == mRootLayout) {
				Toast.makeText(this, "Finished", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// Do nothing.
	}

	@Override
	public void onAnimationCancel(Animator animation) {
		// Do nothing.
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
		// Do nothing.
	}

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
