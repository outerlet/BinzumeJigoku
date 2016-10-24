package jp.onetake.binzumejigoku.view;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import jp.onetake.binzumejigoku.R;

/**
 * 複数の文字列をフェードインアニメーションで表示させるカスタムビュー<br />
 * 一斉にフェード表示させるか、1つずつ順番にフェードさせるかを選択できる
 */
public class FadeInTextView extends LinearLayout {
	private List<TextView> mTextViewList;
	private int mTextSize;
	private int mTextColor;
	private int mSpace;
	private int mDuration;
	private boolean mIsFadeFinished;

	private AnimatorSet mAnimatorSet;

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 */
	public FadeInTextView(Context context) {
		this(context, null);
	}

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param attrs		アトリビュートセット
	 */
	public FadeInTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mIsFadeFinished = false;
		mTextViewList = new ArrayList<>();

		setOrientation(LinearLayout.VERTICAL);

		if (attrs != null) {
			Resources res = context.getResources();

			TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FadeInTextView);

			mTextSize = typedArray.getInteger(
					R.styleable.FadeInTextView_textSizeSp, res.getInteger(R.integer.default_fadein_text_size_sp));

			mTextColor = typedArray.getColor(R.styleable.FadeInTextView_textColor, Color.BLACK);

			mSpace = typedArray.getDimensionPixelSize(
					R.styleable.FadeInTextView_space, res.getDimensionPixelSize(R.dimen.default_fadein_textview_space));

			mDuration = typedArray.getInteger(
					R.styleable.FadeInTextView_duration, res.getInteger(R.integer.fadein_textview_animation_duration));

			typedArray.recycle();
		}
	}

	/**
	 * フェードさせるテキストを1つ追加する
	 * @param text
	 */
	public void addText(String text) {
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

		if (mTextViewList.size() > 0) {
			lp.setMargins(0, mSpace, 0, 0);
		}

		TextView textView = new TextView(getContext());
		textView.setLayoutParams(lp);
		textView.setText(text);
		textView.setTextSize(mTextSize);
		textView.setTextColor(mTextColor);
		textView.setAlpha(0.0f);

		addView(textView);

		mTextViewList.add(textView);
	}

	/**
	 * フェードによるテキスト表示が終了しているかを返却する
	 * @return	テキスト表示が終了していればtrue
	 */
	public boolean isFadeFinished() {
		return mIsFadeFinished;
	}

	/**
	 * フェードアニメーションを開始する<br />
	 * sequentialの値によって一斉にフェードするか、順番にフェードするかが変わる
	 * @param sequential	どのようにフェードさせるか.trueなら順番に、falseなら一斉に
	 * @param delay			フェードが開始するまでの遅延時間(ms)
	 */
	public void start(boolean sequential, long delay) {
		List<Animator> list = new ArrayList<>();
		for (TextView tv : mTextViewList) {
			list.add(ObjectAnimator.ofFloat(tv, "alpha", 0.0f, 1.0f));
		}

		mIsFadeFinished = true;

		mAnimatorSet = new AnimatorSet();

		if (sequential) {
			mAnimatorSet.playSequentially(list);
		} else {
			mAnimatorSet.playTogether(list);
		}

		mAnimatorSet.setDuration(list.size() * mDuration);
		mAnimatorSet.setStartDelay(delay);
		mAnimatorSet.start();
	}
}
