package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.util.ContentsXmlParserTask;

/**
 * 起動ポイントとなるアクティビティ
 */
public class LaunchActivity extends BasicActivity implements ContentsXmlParserTask.XmlParseListener {
	// このアクティビティが、起動シーケンスのうちどの状態にあるかを示す列挙値
	private enum LaunchStatus {
		BackgroundAnimation1,	// 背景1表示中
		BackgroundAnimation2,	// 背景1非表示、背景2表示中
		TitleAnimation,			// タイトル表示中
		WaitingUserInteraction,	// ユーザー操作(=画面タッチ)待ち
		XmlParsing,				// XML解析中
		FinishAnimation,		// アクティビティ終了中
	}

	private ImageView mLaunchImage1;
	private ImageView mLaunchImage2;
	private ViewGroup mLaunchTitles;
	private ImageView mUserInteraction;
	private ProgressBar mProgressBar;

	private LaunchStatus mStatus;

	// AnimatorListenerをActivityに実装するとコードが冗長になるのでこのかたち
	private AnimatorListenerAdapter mAnimatorListener = new AnimatorListenerAdapter() {
		@Override
		public void onAnimationEnd(Animator animation) {
			// 白画面 -> 画像1表示
			if (mStatus == LaunchStatus.BackgroundAnimation1) {
				mStatus = LaunchStatus.BackgroundAnimation2;

				mLaunchImage2.setVisibility(View.VISIBLE);

				Animator anim1 = createAnimator(mLaunchImage1, false);
				Animator anim2 = createAnimator(mLaunchImage2, true);

				AnimatorSet anims = new AnimatorSet();
				anims.playTogether(anim1, anim2);
				anims.setStartDelay(1000);
				anims.setDuration(1000);
				anims.addListener(mAnimatorListener);
				anims.start();
			// 画像1非表示＋画像2表示
			} else if (mStatus == LaunchStatus.BackgroundAnimation2) {
				mStatus = LaunchStatus.TitleAnimation;

				mLaunchImage1.setVisibility(View.INVISIBLE);
				mLaunchTitles.setVisibility(View.VISIBLE);

				createAnimator(mLaunchTitles, true, 1000, 1000).start();
			// タイトル表示
			// ユーザー操作待ちメッセージ表示
			} else if (mStatus == LaunchStatus.TitleAnimation) {
				mStatus = LaunchStatus.WaitingUserInteraction;

				ObjectAnimator anim = createAnimator(mUserInteraction, true);
				anim.setStartDelay(1000);
				anim.setDuration(1000);
				anim.setRepeatMode(ObjectAnimator.REVERSE);
				anim.setRepeatCount(ObjectAnimator.INFINITE);

				mUserInteraction.setVisibility(View.VISIBLE);
				mUserInteraction.setTag(anim);

				anim.start();
			// タイトルと画像2非表示
			// 自身を破棄するために FLAG_ACTIVITY_NEW_TASK + FLAG_ACTIVITY_CLEAR_TASK を使うと
			// Activityの遷移がまるわかりになるのでベタなやり方でメインメニューに遷移
			} else if (mStatus == LaunchStatus.FinishAnimation) {
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

		mLaunchImage1 = (ImageView)findViewById(R.id.imageview_launch_background_1);
		mLaunchImage2 = (ImageView)findViewById(R.id.imageview_launch_background_2);
		mLaunchTitles = (ViewGroup)findViewById(R.id.viewgroup_launch_titles);
		mUserInteraction = (ImageView)findViewById(R.id.imageview_user_interaction);
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar_loading_contents);

		mLaunchImage1.setVisibility(View.VISIBLE);

		mStatus = LaunchStatus.BackgroundAnimation1;

		createAnimator(mLaunchImage1, true, 1000, 1000).start();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * このActivityでバックキーは無効にしておく
	 * </p>
	 */
	@Override
	public void onBackPressed() {
		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// ユーザー操作待ち状態であればプログレスバーを表示してXMLの解析を開始する
		if (event.getAction() == MotionEvent.ACTION_UP && mStatus == LaunchStatus.WaitingUserInteraction) {
			mStatus = LaunchStatus.FinishAnimation;

			((ObjectAnimator)mUserInteraction.getTag()).cancel();
			mUserInteraction.setVisibility(View.INVISIBLE);
			mProgressBar.setVisibility(View.VISIBLE);

			ContentsXmlParserTask parserTask = new ContentsXmlParserTask(this);
			parserTask.setListener(this);
			parserTask.execute(getString(R.string.filename_contents));

			return true;
		}

		return super.onTouchEvent(event);
	}

	/**
	 * viewの表示状態(透過から非透過、あるいはその逆)を変更するアニメータオブジェクトを生成する<br />
	 * AnimatorSet等で使用することを考慮し、このメソッドで生成したObjectAnimatorにはリスナはつかない
	 * @param view		表示状態を変更するview
	 * @param visible	非表示->表示ならtrue、逆ならfalse
	 * @return	パラメータの内容に基づくObjectAnimatorオブジェクト
	 */
	private ObjectAnimator createAnimator(View view, boolean visible) {
		return ObjectAnimator.ofFloat(view, "alpha", visible ? 0.0f : 1.0f, visible ? 1.0f : 0.0f);
	}

	/**
	 * viewの表示状態(透過から非透過、あるいはその逆)を変更するアニメータオブジェクトを生成する<br />
	 * このメソッドで生成したObjectAnimatorにはリスナがつく
	 * @param view		表示状態を変更するview
	 * @param visible	非表示->表示ならtrue、逆ならfalse
	 * @param delay		アニメーションの開始を遅延させる時間(msec)
	 * @param duration	アニメーションにかける時間(msec)
	 * @return	パラメータの内容に基づくObjectAnimatorオブジェクト
	 */
	private ObjectAnimator createAnimator(View view, boolean visible, long delay, long duration) {
		ObjectAnimator anim = createAnimator(view, visible);
		anim.setDuration(duration);
		anim.setStartDelay(delay);
		anim.addListener(mAnimatorListener);

		return anim;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onParseFinished(boolean executed) {
		mProgressBar.setVisibility(View.INVISIBLE);

		ObjectAnimator anim1 = createAnimator(mLaunchTitles, false);
		ObjectAnimator anim2 = createAnimator(mLaunchImage2, false);

		AnimatorSet anims = new AnimatorSet();
		anims.playSequentially(anim1, anim2);
		anims.setStartDelay(1000);
		anims.setDuration(1000);
		anims.addListener(mAnimatorListener);
		anims.start();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onExceptionOccurred(Exception e) {
		e.printStackTrace();

		Toast.makeText(LaunchActivity.this, R.string.error_app_initialize, Toast.LENGTH_LONG).show();
		finish();
	}
}
