package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;

public class SaveActivity extends BasicActivity implements View.OnClickListener, ObjectAnimator.AnimatorListener {
	private final int ANIMATION_DURATION_BGCOLOR	= 300;
	private final int ANIMATION_DURATION_MOVE_Y		= 300;

	private LinearLayout mRootLayout;
	private RelativeLayout mButtonLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);

		mRootLayout = (LinearLayout)findViewById(R.id.layout_root);
		mButtonLayout = (RelativeLayout)findViewById(R.id.layout_save_buttons);

		findViewById(R.id.button_save1).setOnClickListener(this);
		findViewById(R.id.button_save2).setOnClickListener(this);
		findViewById(R.id.button_save3).setOnClickListener(this);

		final float moveY = getResources().getDimensionPixelSize(R.dimen.save_button_move_y) * -1.0f;

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
		switch (view.getId()) {
			case R.id.button_save1:
				Toast.makeText(this, "Slot 1", Toast.LENGTH_SHORT).show();
				break;
			case R.id.button_save2:
				Toast.makeText(this, "Slot 2", Toast.LENGTH_SHORT).show();
				break;
			case R.id.button_save3:
				Toast.makeText(this, "Slot 3", Toast.LENGTH_SHORT).show();
				break;
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
}
