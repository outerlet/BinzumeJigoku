package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.Title;

/**
 * セクション(章)表示用のView<br />
 * (1)「透明->不透明」(2)「不透明のまま待機」(3)「不透明->透明」という遷移で表示状態が変わる<br />
 * XMLに指定できる属性値についてはそれぞれ以下の通り<br />
 * <dl>
 *     <dt>duration</dt>
 *     <dd>各状態のためのかける時間をミリ秒で指定</dd>
 *     <dt>titleSize</dt>
 *     <dd>タイトルのテキストサイズをdimensionで指定</dd>
 *     <dt>titleColor</dt>
 *     <dd>タイトルのテキスト色を指定</dd>
 * </dl>
 */
public class ContentsTitleView extends TimerView {
	/**
	 * タイトルの1文字を表現するためのクラス<br />
	 * 自身の表示にかける時間や表示までの遅延時間、描画に使用するPaintオブジェクトなどを保持する
	 */
	private class Letter {
		public String letter;
		public float duration;
		public float delay;
		private Paint mPaint;

		public Letter(String letter, float duration, float delay) {
			this.letter = letter;
			this.duration = duration;
			this.delay = delay;

			mPaint = new Paint();
			mPaint.setTextSize(mTextSize);
			mPaint.setColor(mTextColor);
		}

		public Paint getPaintByAlpha(int alpha) {
			mPaint.setAlpha(alpha);
			return mPaint;
		}
	}

	private static int DEFAULT_DURATION			= 3000;			// 文字の描画にかけるデフォルトの時間
	private static int DEFAULT_COLOR			= Color.BLACK;	// デフォルトの文字色
	private static int ALPHA_OPAQUE				= 255;			// 完全に不透明な状態を示すアルファ値
	private static int ALPHA_TRANSLUCENT		= 0;			// 完全に透明な状態を示すアルファ値
	private static float DURATION_ADJUSTMENT	= 2.0f;			// 文字の描画にかける時間を補正するための値

	private long mDuration;
	private int mTextColor;
	private float mTextSize;
	private Letter[] mTitleLetters;

	public ContentsTitleView(Context context) {
		this(context, null);
	}

	public ContentsTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ContentsTitleView);

			mDuration = array.getInt(R.styleable.ContentsTitleView_duration, DEFAULT_DURATION);

			mTextSize = array.getDimensionPixelSize(
					R.styleable.ContentsTitleView_titleSize,
					context.getResources().getDimensionPixelSize(R.dimen.default_title_text_size));

			mTextColor = array.getColor(R.styleable.ContentsTitleView_titleColor, DEFAULT_COLOR);

			array.recycle();
		}
	}

	@Override
	protected boolean executeDraw(Canvas canvas, int calledCount) {
		long elapsed = getElapsedMillis();

		float startX = (canvas.getWidth() / 2.0f) - ((mTitleLetters.length * mTextSize) / 2.0f);

		for (int i = 0 ; i < mTitleLetters.length ; i++) {
			Letter letter = mTitleLetters[i];

			int alpha;

			if (elapsed <= mDuration) {
				if (elapsed >= letter.delay && elapsed <= letter.delay + letter.duration) {
					float ratio = (elapsed - letter.delay) / letter.duration;
					alpha = (int)(ALPHA_OPAQUE * ((ratio < 1.0f) ? ratio : 1.0f));
				} else if (elapsed > letter.delay + letter.duration) {
					alpha = ALPHA_OPAQUE;
				} else {
					alpha = ALPHA_TRANSLUCENT;
				}
			} else if (elapsed > mDuration && elapsed <= mDuration * 2) {
				alpha = ALPHA_OPAQUE;
			} else {
				long e = elapsed - mDuration * 2;

				if (e >= letter.delay && e <= letter.delay + letter.duration) {
					float ratio = 1.0f - ((e - letter.delay) / letter.duration);
					alpha = (int)(ALPHA_OPAQUE * ((ratio > 0.0f) ? ratio : 0.0f));
				} else if (e > letter.delay + letter.duration) {
					alpha = ALPHA_TRANSLUCENT;
				} else {
					alpha = ALPHA_OPAQUE;
				}
			}

			canvas.drawText(letter.letter, startX + mTextSize * i, canvas.getHeight() / 2.0f, letter.getPaintByAlpha(alpha));
		}

		return (elapsed <= mDuration * 3);
	}

	public void setTitle(Title title) {
		String titleText = title.getTitle();
		int textCount = titleText.length();

		float duration = (mDuration / textCount) * DURATION_ADJUSTMENT;
		float delay = (duration * textCount - mDuration) / (textCount - 1);

		mTitleLetters = new Letter[textCount];
		for (int i = 0 ; i < textCount ; i++) {
			mTitleLetters[i] = new Letter(titleText.substring(i, i + 1), duration, (duration - delay) * i);
		}
	}
}
