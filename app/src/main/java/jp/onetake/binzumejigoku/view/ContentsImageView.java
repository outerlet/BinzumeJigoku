package jp.onetake.binzumejigoku.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import jp.onetake.binzumejigoku.R;
import jp.onetake.binzumejigoku.contents.element.Image;

/**
 * 画像を表示する<br />
 * 1つの画像を表示するためのViewだがクロスフェードがかけられるように、内部で2つのImageViewを保持している
 */
public class ContentsImageView extends FrameLayout {
	/**
	 * このViewでエフェクト(フェードやカット)が発生したイベントを捕捉するためのリスナ
	 */
	public interface EffectListener {
		/**
		 * このViewでエフェクトが発生したタイミングで呼び出されるリスナメソッド
		 * @param view
		 */
		void onEffectFinished(ContentsImageView view);
	}

	// このViewが内部に持つImageViewは2つ
	private final int NUMBER_OF_LAYERS = 2;

	private ImageView[] mImageViews;
	private Image mImage;
	private EffectListener mListener;

	/**
	 * {@inheritDoc}
	 */
	public ContentsImageView(Context context) {
		this(context, null);
	}

	/**
	 * {@inheritDoc}
	 */
	public ContentsImageView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setLayoutParams(new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

		mImageViews = new ImageView[NUMBER_OF_LAYERS];

		for (int i = 0 ; i < mImageViews.length ; i++) {
			ImageView imageView = (ImageView)LayoutInflater
					.from(getContext()).inflate(R.layout.view_contents_image_layer, this, false);
			this.addView(imageView);

			mImageViews[i] = imageView;
		}
	}

	/**
	 * このViewに画像を表示させるためのImageオブジェクトをセットする
	 * @param image	Viewに画像を表示させるためのImageオブジェクト
	 */
	public void setImage(Image image) {
		mImage = image;
	}

	/**
	 * このViewで発生したイベントを捕捉するリスナをセットする
	 * @param listener	イベントを捕捉させるリスナオブジェクト
	 */
	public void setListener(EffectListener listener) {
		mListener = listener;
	}

	/**
	 * 画像の表示シーケンスを指定した遅延時間だけ待った上で開始する<br />
	 * Imageオブジェクト次第でフェードやカットなどの効果が発生する
	 * @param delay	表示シーケンス開始までの遅延時間(ms)
	 */
	public void start(long delay) {
		ImageView usedView = getImageViewByVisibility(View.VISIBLE);
		ImageView unusedView = getImageViewByVisibility(View.INVISIBLE);

		Bitmap bitmap = mImage.getBitmap();

		// 次に表示すべき画像がある
		if (bitmap != null) {
			unusedView.setImageBitmap(bitmap);
			unusedView.setVisibility(View.VISIBLE);

			// 両方とも使ってない
			if (usedView == null) {
				if (mImage.getEffectType() == Image.EffectType.Fade) {
					ObjectAnimator anim = ObjectAnimator.ofFloat(unusedView, "alpha", 0.0f, 1.0f);
					anim.setDuration(mImage.getDuration());
					anim.setStartDelay(delay);
					anim.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							if (mListener != null) {
								mListener.onEffectFinished(ContentsImageView.this);
							}
						}
					});
					anim.start();
				} else if (mImage.getEffectType() == Image.EffectType.Cut) {
					unusedView.setAlpha(1.0f);
				}
			// どっちかが使われている
			} else {
				if (mImage.getEffectType() == Image.EffectType.Fade) {
					final ImageView animView = usedView;

					AnimatorSet animSet = new AnimatorSet();
					animSet.playTogether(
							ObjectAnimator.ofFloat(unusedView, "alpha", 0.0f, 1.0f),
							ObjectAnimator.ofFloat(usedView, "alpha", 1.0f, 0.0f));
					animSet.setDuration(mImage.getDuration());
					animSet.addListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							animView.setVisibility(View.INVISIBLE);

							if (mListener != null) {
								mListener.onEffectFinished(ContentsImageView.this);
							}
						}
					});
					animSet.start();
				} else if (mImage.getEffectType() == Image.EffectType.Cut) {
					unusedView.setAlpha(1.0f);

					usedView.setAlpha(0.0f);
					usedView.setVisibility(View.INVISIBLE);
				}
			}
		// 次に表示すべき画像がない(非表示にする)
		} else {
			if (mImage.getEffectType() == Image.EffectType.Fade) {
				final ImageView animView = usedView;

				ObjectAnimator anim = ObjectAnimator.ofFloat(usedView, "alpha", 1.0f, 0.0f);
				anim.setDuration(mImage.getDuration());
				anim.addListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						animView.setVisibility(View.INVISIBLE);

						if (mListener != null) {
							mListener.onEffectFinished(ContentsImageView.this);
						}
					}
				});
				anim.start();
			} else if (mImage.getEffectType() == Image.EffectType.Cut) {
				usedView.setAlpha(0.0f);
				usedView.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * エフェクト等は一切無視して画像を即時に表示・非表示にする
	 */
	public void immediate() {
		ImageView usedView = getImageViewByVisibility(View.VISIBLE);
		ImageView unusedView = getImageViewByVisibility(View.INVISIBLE);

		Bitmap bitmap = mImage.getBitmap();

		// 次に表示すべき画像がある
		if (bitmap != null) {
			unusedView.setImageBitmap(bitmap);
			unusedView.setVisibility(View.VISIBLE);
			unusedView.setAlpha(1.0f);

			if (usedView != null) {
				usedView.setAlpha(0.0f);
				usedView.setVisibility(View.INVISIBLE);
			}
		// 次に表示すべき画像がない
		} else {
			usedView.setAlpha(0.0f);
			usedView.setVisibility(View.INVISIBLE);
		}
	}

	private ImageView getImageViewByVisibility(int visibility) {
		for (ImageView imageView : mImageViews) {
			if (imageView.getVisibility() == visibility) {
				return imageView;
			}
		}

		return null;
	}
}
