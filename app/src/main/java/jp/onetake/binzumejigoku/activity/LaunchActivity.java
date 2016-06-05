package jp.onetake.binzumejigoku.activity;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.common.ContentsInterface;
import jp.onetake.binzumejigoku.contents.xml.ContentsXmlParser;

/**
 * 起動ポイントとなるインスタンス
 */
public class LaunchActivity extends BasicActivity implements Animator.AnimatorListener, ContentsXmlParser.ParserListener {
	private final int ANIMATION_DURATION		= 1000;
	private final int ANIMATION_DELAY_FORWARD	= 500;
	private final int ANIMATION_DELAY_BACKWARD	= 1000;

	private ImageView mLaunchImage;
	private ProgressBar mProgressBar;
	private ContentsXmlParser mXmlParser;
	private boolean mIsForward;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_launch);

		// コンテンツ操作用オブジェクトを初期化
		ContentsInterface.getInstance().initialize(getApplicationContext());

		mLaunchImage = (ImageView)findViewById(R.id.imageview_launch_title);
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar_loading_contents);
		mXmlParser = new ContentsXmlParser(this);

		mIsForward = false;

		startAnimation(true);
	}

	@Override
	public void onBackPressed() {
		// このActivityでバックキーは無効
		return;
	}

	private void startAnimation(boolean forward) {
		mIsForward = forward;

		ObjectAnimator anim = ObjectAnimator.ofFloat(
				mLaunchImage, "alpha", forward ? 0.0f : 1.0f, forward ? 1.0f : 0.0f);
		anim.setDuration(ANIMATION_DURATION);
		anim.setStartDelay(forward ? ANIMATION_DELAY_FORWARD : ANIMATION_DELAY_BACKWARD);
		anim.addListener(this);
		anim.start();
	}

	@Override
	public void onParseFinished() {
		mProgressBar.setVisibility(View.INVISIBLE);
		startAnimation(false);
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// Do nothing.
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (mIsForward) {
			if (!ContentsInterface.getInstance().isXmlParsed()) {
				mProgressBar.setVisibility(View.VISIBLE);

				try {
					mXmlParser.setListener(LaunchActivity.this);
					mXmlParser.parse(getString(R.string.filename_contents));
				} catch (IOException | XmlPullParserException ex) {
					ex.printStackTrace();
					mProgressBar.setVisibility(View.INVISIBLE);
				}
			} else {
				startAnimation(false);
			}
		} else {
			// 自身を破棄するために FLAG_ACTIVITY_NEW_TASK + FLAG_ACTIVITY_CLEAR_TASK を使うと
			// Activityの遷移がまるわかりになるのでベタなやり方でメインメニューに遷移
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
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
