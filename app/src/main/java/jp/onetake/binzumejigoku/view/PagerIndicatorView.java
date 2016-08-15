package jp.onetake.binzumejigoku.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import jp.onetake.binzumejigoku.R;

/**
 * ViewPagerなどで利用するためのインジケータ。総ページ数と現在のページを視覚的に表示するためのもの<br />
 * iOSのUIPageIndicatorのマネっこクラス
 */
public class PagerIndicatorView extends View {
	private int mPageCount;
	private int mActiveIndex;
	private float mRadius;
	private float mSpace;
	private int mActiveColor;
	private int mInactiveColor;

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 */
	public PagerIndicatorView(Context context) {
		this(context, null);
	}

	/**
	 * コンストラクタ
	 * @param context	コンテキスト
	 * @param attrs		アトリビュートセット
	 */
	public PagerIndicatorView(Context context, AttributeSet attrs) {
		super(context, attrs);

		mActiveIndex = 0;

		if (attrs != null) {
			TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicatorView);

			mRadius = array.getDimensionPixelSize(
					R.styleable.PagerIndicatorView_radius, context.getResources().getDimensionPixelSize(R.dimen.pager_indicator_radius));

			mSpace = array.getDimensionPixelSize(
					R.styleable.PagerIndicatorView_space, context.getResources().getDimensionPixelSize(R.dimen.pager_indicator_space));

			mActiveColor = array.getColor(
					R.styleable.PagerIndicatorView_activeColor, context.getResources().getColor(R.color.white));

			mInactiveColor = array.getColor(
					R.styleable.PagerIndicatorView_inactiveColor, context.getResources().getColor(R.color.light_gray));

			array.recycle();
		} else {
			mRadius = context.getResources().getDimensionPixelSize(R.dimen.pager_indicator_radius);
			mSpace = context.getResources().getDimensionPixelSize(R.dimen.pager_indicator_space);
			mActiveColor = context.getResources().getColor(R.color.white);
			mInactiveColor = context.getResources().getColor(R.color.light_gray);
		}
	}

	/**
	 * アクティブなページであることを示すインジケータの色
	 * @param activeColor	インジケータの色を示すint値
	 */
	public void setActiveColor(int activeColor) {
		mActiveColor = activeColor;
	}

	/**
	 * インジケータの大きさ(=半径)を設定する
	 * @param radius	インジケータの半径
	 */
	public void setRadius(float radius) {
		mRadius = radius;
	}

	/**
	 * インジケータ同士の間隔を設定する
	 * @param distance	インジケータ同士の間隔
	 */
	public void setDistance(float distance) {
		mSpace = distance;
	}

	/**
	 * ページの総数を設定する
	 * @param pageCount	ページの総数
	 */
	public void setPageCount(int pageCount) {
		mPageCount = pageCount;
	}

	/**
	 * アクティブなページがどこかをセットする
	 * @param activeIndex	アクティブなページを示すインデックス値
	 */
	public void setActiveIndex(int activeIndex) {
		mActiveIndex = activeIndex;

		invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		setMeasuredDimension(
				widthMeasureSpec,
				MeasureSpec.makeMeasureSpec((int)mRadius * 4, MeasureSpec.EXACTLY));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		Paint activePaint = new Paint();
		activePaint.setColor(mActiveColor);

		Paint inactivePaint = new Paint();
		inactivePaint.setColor(mInactiveColor);

		float totalWidth = mRadius * 2 * (mPageCount - 1) + mSpace * (mPageCount - 1);
		float startX = canvas.getWidth() / 2 - totalWidth / 2 + mRadius;
		float posY = canvas.getHeight() / 2;

		for (int i = 0; i < mPageCount; i++) {
			float posX = startX + mSpace * i;
			canvas.drawCircle(posX, posY, mRadius, (i == mActiveIndex) ? activePaint : inactivePaint);
		}
	}
}
