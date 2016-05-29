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
	private ImageView mLaunchImage;
	private ProgressBar mProgressBar;

	private ContentsXmlParser mXmlParser;
	private boolean mIsForwarding = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_launch);

		// コンテンツ操作用オブジェクトを初期化
		ContentsInterface.getInstance().initialize(getApplicationContext());

		mLaunchImage = (ImageView)findViewById(R.id.imageview_launch_title);
		mProgressBar = (ProgressBar)findViewById(R.id.progressbar_loading_contents);
		mXmlParser = new ContentsXmlParser(this);

		startAnimation(true);
	}

	@Override
	public void onBackPressed() {
		// このActivityでバックキーは無効
		return;
	}

	private void startAnimation(boolean isForwarding) {
		float start = isForwarding ? 0.0f : 1.0f;
		float end = isForwarding ? 1.0f : 0.0f;

		ObjectAnimator anim = ObjectAnimator.ofFloat(mLaunchImage, "alpha", start, end);
		anim.setDuration(2000);
		anim.setStartDelay(1000);
		anim.addListener(this);
		anim.start();

		mIsForwarding = isForwarding;
	}

	@Override
	public void onAnimationStart(Animator animation) {
		// Do nothing.
	}

	@Override
	public void onAnimationEnd(Animator animation) {
		if (mIsForwarding) {
			if (!ContentsInterface.getInstance().isXmlParsed()) {
				mProgressBar.setVisibility(View.VISIBLE);

				try {
					mXmlParser.setListener(this);
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

	@Override
	public void onParseFinished() {
		mProgressBar.setVisibility(View.INVISIBLE);
		startAnimation(false);
	}
}
