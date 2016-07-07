package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.xml.ContentsXmlParserTask;

/**
 * 起動ポイントとなるアクティビティ
 */
public class LaunchActivity extends BasicActivity implements ContentsXmlParserTask.XmlParseListener {
	private ImageView mLaunchImage;
	private ProgressBar mProgressBar;
	private ContentsXmlParserTask mXmlParserTask;
	private boolean mIsForward;

	// AnimatorListenerをActivityに実装するとコードが冗長になるのでこのかたち
	private AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
		@Override
		public void onAnimationEnd(Animator animation) {
			if (mIsForward) {
				mProgressBar.setVisibility(View.VISIBLE);
				mXmlParserTask.execute(getString(R.string.filename_contents));
			} else {
				// 自身を破棄するために FLAG_ACTIVITY_NEW_TASK + FLAG_ACTIVITY_CLEAR_TASK を使うと
				// Activityの遷移がまるわかりになるのでベタなやり方でメインメニューに遷移
				startActivity(new Intent(LaunchActivity.this, MainActivity.class));
				finish();
			}
		}
	};

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_launch);

		// Preferencesの初期値を設定
		PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

		mLaunchImage = (ImageView)findViewById(R.id.imageview_launch_title);
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar_loading_contents);

		mXmlParserTask = new ContentsXmlParserTask(this);
		mXmlParserTask.setListener(this);

		startAnimation(true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == getResources().getInteger(R.integer.request_code_tutorial_activity)) {
			ContentsInterface.getInstance().markAsTutorialFinished();
			startAnimation(false);
		}
	}

	/**
	 * <p>
	 * このActivityでバックキーは無効にしておく
	 * </p>
	 * {@inheritDoc}
	 */
	@Override
	public void onBackPressed() {
		return;
	}

	/**
	 * 起動時アニメーションを順方向または逆方向に開始する
	 * @param forward	順方向にアニメーションさせるならtrue
	 */
	private void startAnimation(boolean forward) {
		mIsForward = forward;

		ObjectAnimator anim = ObjectAnimator.ofFloat(
				mLaunchImage, "alpha", forward ? 0.0f : 1.0f, forward ? 1.0f : 0.0f);
		anim.setDuration(getResources().getInteger(R.integer.launch_animation_duration));
		anim.setStartDelay(getResources().getInteger(
				forward ? R.integer.launch_animation_delay_forward : R.integer.launch_animation_delay_backward));
		anim.addListener(mAnimatorListener);
		anim.start();
	}

	@Override
	public void onParseFinished(boolean executed) {
		mProgressBar.setVisibility(View.INVISIBLE);

		if (ContentsInterface.getInstance().isTutorialFinished()) {
			startAnimation(false);
		} else {
			startActivityForResult(
					new Intent(this, TutorialActivity.class),
					getResources().getInteger(R.integer.request_code_tutorial_activity));
		}
	}

	@Override
	public void onExceptionOccurred(Exception e) {
		e.printStackTrace();

		Toast.makeText(LaunchActivity.this, R.string.error_app_initialize, Toast.LENGTH_LONG).show();
		finish();
	}
}
