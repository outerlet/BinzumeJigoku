package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;

public class SaveActivity extends BasicActivity
		implements View.OnClickListener, ObjectAnimator.AnimatorListener {
	private RelativeLayout mRootLayout;
	private Button mSaveButton1;
	private Button mSaveButton2;
	private Button mSaveButton3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_save);

		mRootLayout = (RelativeLayout)findViewById(R.id.layout_root);
		mSaveButton1 = (Button)findViewById(R.id.button_save1);
		mSaveButton2 = (Button)findViewById(R.id.button_save2);
		mSaveButton3 = (Button)findViewById(R.id.button_save3);

		ObjectAnimator anim = ObjectAnimator.ofFloat(mRootLayout, "alpha", 0.0f, 1.0f);
		anim.setDuration(500);
		anim.addListener(this);
		anim.start();
	}

	@Override
	public void finish() {
		super.finish();

		overridePendingTransition(0, 0);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.button_save1:
				break;
			case R.id.button_save2:
				break;
			case R.id.button_save3:
				break;
		}
	}

	@Override
	public void onAnimationStart(Animator animation) {
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
	public void onAnimationCancel(Animator animation) {
	}

	@Override
	public void onAnimationRepeat(Animator animation) {
	}
}
